package com.cheatsheet.quiz.feature.interview.service.flow;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.SessionRecord;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.persistence.SessionHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * История завершённых сессий: запись при завершении и чтение для аналитики.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Сводка сессии жила только в {@code HttpSession}. Экран итогов читал её один раз и терял
 * при следующем старте, перезагрузке страницы или закрытии вкладки — проверено на живом
 * приложении: повторный заход на {@code /session-summary} после старта новой сессии уже
 * ничего не показывал. Значит вопрос «сколько я занимался и как шёл» не имел ответа нигде.
 *
 * <h2>Что здесь есть и чего нет</h2>
 * Пишутся ИТОГИ сессии, а не её ответы: подробности каждой попытки уже лежат в
 * {@code attempt} и {@code review_state}, и вторая их копия расходилась бы с первой.
 * Пустая сессия (ни одного ответа) не записывается: строка «0 из 0» в истории — это шум,
 * который выглядит как занятие, но им не является.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionHistoryService {

    SessionHistoryRepository repository;
    Clock clock;

    /** Сколько последних сессий показывает аналитика. */
    public static final int RECENT_LIMIT = 10;

    /**
     * Записывает завершённую сессию.
     *
     * <p>Ошибка записи не должна ронять завершение сессии: человек только что закончил
     * работу, и потеря строки в истории — меньшая беда, чем 500 вместо итогов.</p>
     */
    public void record(SessionSummary summary) {
        if (summary == null) {
            return;
        }
        int answered = summary.getCorrectCount() + summary.getWrongCount() + summary.getUnknownCount();
        if (answered == 0) {
            return;
        }
        try {
            repository.save(new SessionRecord(
                    0L,
                    summary.getCompletedAt() == null ? Instant.now(clock) : summary.getCompletedAt(),
                    summary.getMode() == null ? InterviewMode.TRAINING : summary.getMode(),
                    summary.getTotalQuestions(),
                    summary.getCorrectCount(),
                    summary.getWrongCount(),
                    summary.getUnknownCount(),
                    summary.getDuration() == null ? Duration.ZERO : summary.getDuration(),
                    summary.getGamification() == null ? 0L : summary.getGamification().sessionXp()));
        } catch (RuntimeException e) {
            log.warn("session_history_write_failed mode={} answered={}: {}",
                    summary.getMode(), answered, e.getMessage());
        }
    }

    /** Последние сессии, свежие сверху. */
    public List<SessionRecord> recent() {
        try {
            return repository.findRecent(RECENT_LIMIT);
        } catch (RuntimeException e) {
            log.warn("session_history_read_failed: {}", e.getMessage());
            return List.of();
        }
    }

    /** Сколько сессий записано всего. */
    public long total() {
        try {
            return repository.count();
        } catch (RuntimeException e) {
            return 0L;
        }
    }
}
