package com.cheatsheet.quiz.feature.interview.service.flow;

import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.PausedSessionInfo;
import com.cheatsheet.quiz.persistence.PausedSessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Clock;
import java.util.Optional;

/**
 * Пауза/возобновление активной сессии тренировки (FLOW-01).
 *
 * <p>Приостановленная сессия персистится в БД, чтобы пережить перезапуск
 * приложения (в отличие от HTTP-сессии в памяти). Само состояние сохраняется
 * Java-сериализацией {@link InterviewSession} — тем же механизмом, которым объект
 * уже персистится в HTTP-сессию, поэтому доменная модель не меняется.</p>
 *
 * <p>Приложение single-user → одновременно не более одной паузы (см.
 * {@link PausedSessionRepository}). Повторная пауза перезаписывает предыдущую.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PauseService {

    PausedSessionRepository pausedSessionRepository;
    Clock clock;

    /**
     * Ставит активную сессию на паузу: сериализует и сохраняет её. Завершённые
     * и пустые сессии игнорируются (паузить нечего). Очистку HTTP-сессии делает
     * вызывающий (контроллерный слой).
     */
    public void pause(InterviewSession session) {
        if (session == null || session.isFinished()) {
            return;
        }
        byte[] blob = serialize(session);
        pausedSessionRepository.save(
                blob,
                session.getMode(),
                session.getTopic(),
                session.getTotal(),
                session.getIndex(),
                clock.instant());
    }

    /**
     * Возобновляет приостановленную сессию: десериализует её и УДАЛЯЕТ из БД
     * (одноразовое потребление). Возвращает пусто, если паузы нет или блоб
     * несовместим (тогда битый блоб тоже удаляется).
     */
    public Optional<InterviewSession> resume() {
        Optional<byte[]> blob = pausedSessionRepository.findBlob();
        if (blob.isEmpty()) {
            return Optional.empty();
        }
        InterviewSession session = deserialize(blob.get());
        pausedSessionRepository.clear();
        return Optional.ofNullable(session);
    }

    /**
     * Метаданные приостановленной сессии для баннера «продолжить?» (без десериализации).
     */
    public Optional<PausedSessionInfo> pausedInfo() {
        return pausedSessionRepository.findInfo();
    }

    /**
     * Отбрасывает приостановленную сессию без возобновления.
     */
    public void discard() {
        pausedSessionRepository.clear();
    }

    private byte[] serialize(InterviewSession session) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(session);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сериализовать сессию для паузы", e);
        }
    }

    private InterviewSession deserialize(byte[] blob) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(blob))) {
            Object read = ois.readObject();
            return read instanceof InterviewSession session ? session : null;
        } catch (IOException | ClassNotFoundException e) {
            log.warn("Не удалось десериализовать приостановленную сессию (несовместимый формат?): {}", e.getMessage());
            return null;
        }
    }
}
