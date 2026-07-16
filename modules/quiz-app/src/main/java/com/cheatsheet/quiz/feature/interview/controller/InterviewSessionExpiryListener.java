package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * FLOW-04: автосейв активной сессии при истечении HTTP-session (таймаут/инвалидация).
 *
 * <p>Без него истёкшая сессия — это молчаливая потеря прогресса: пользователь
 * возвращается и попадает на стартовый экран, будто марафона не было. Здесь мы
 * переиспользуем персистентность паузы (FLOW-01): при уничтожении сессии с активной
 * незавершённой {@link InterviewSession} сохраняем её в {@code paused_session}, и
 * пользователь на {@code /} видит тот же resume-баннер, что и после явной паузы
 * («есть незавершённая сессия — продолжить»). Так истечение перестаёт быть тупиком
 * и становится восстановимым состоянием (UX-09/UX-11).</p>
 *
 * <p>Гард {@code index > 0} — не поднимать баннер для сессий, где ничего не отвечено
 * (открыл и ушёл): восстанавливать нечего, это был бы шум. Явная пауза такого гарда
 * не имеет намеренно — там пользователь сам просит сохранить.</p>
 *
 * <p>Событие приходит из фонового reaper-потока контейнера (не request-thread):
 * {@link PauseService} пишет через JdbcTemplate (auto-commit), ambient-транзакция не
 * нужна. Любой сбой автосейва глотаем — он не должен ломать штатное завершение
 * сессии.</p>
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewSessionExpiryListener implements HttpSessionListener {

    PauseService pauseService;
    HttpSessionStateService httpSessionStateService;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        try {
            InterviewSession session = httpSessionStateService.getInterviewSession(se.getSession());
            if (session == null || session.isFinished() || session.getIndex() <= 0) {
                return;
            }
            pauseService.pause(session);
            log.debug("Автосохранена истёкшая сессия для resume (отвечено {} из {})",
                    session.getIndex(), session.getTotal());
        } catch (RuntimeException e) {
            // Автосейв — best-effort: сбой не должен мешать уничтожению сессии.
            log.warn("Не удалось автосохранить истёкшую сессию: {}", e.getMessage());
        }
    }
}
