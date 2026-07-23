package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.domain.DailyProgress;
import com.cheatsheet.quiz.persistence.DailyActivityRepository;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Регресс на транзакционную фазу геймификационных слушателей {@link DailyStreakService}
 * и {@link ExperienceService}. Оба слушают {@link AnswerEvent}, издаваемый внутри
 * {@code @Transactional} submit-методов. Раньше они были {@code @EventListener}
 * (синхронно в ТОЙ ЖЕ TX) — SQL-сбой геймификации переводил TX ответа в aborted
 * (25P02), и весь ответ (включая review_state) откатывался, несмотря на локальный
 * try/catch. Теперь {@code @TransactionalEventListener(AFTER_COMMIT)}: слушатель
 * физически не выполняется внутри первичной TX.
 *
 * <p>Детерминированный дискриминатор фаз (без необходимости воспроизводить реальный
 * 25P02): на ОТКАТЕ издающей транзакции AFTER_COMMIT-слушатель НЕ срабатывает, тогда
 * как синхронный {@code @EventListener} сработал бы до решения об откате.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class GamificationListenerTransactionalPhaseTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    DailyActivityRepository dailyActivityRepository;

    private AnswerEvent sampleAnswerEvent() {
        return new AnswerEvent(this, 1L, 0L, 0L, true,
                "вопрос", "выбранный", "правильный", "разбор", "topic");
    }

    private int questionsAnsweredToday() {
        DailyProgress today = dailyActivityRepository.findToday();
        return today == null ? 0 : today.questionsAnswered();
    }

    @Test
    void listenerDoesNotFireWhenPublishingTransactionRollsBack() {
        int before = questionsAnsweredToday();

        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            eventPublisher.publishEvent(sampleAnswerEvent());
            status.setRollbackOnly();
        });

        // AFTER_COMMIT: откат издающей TX → слушатель не выполнился → счётчик не вырос.
        // Со старым @EventListener increment случился бы синхронно ДО отката.
        assertThat(questionsAnsweredToday()).isEqualTo(before);
    }

    @Test
    void listenerFiresAfterPublishingTransactionCommits() {
        int before = questionsAnsweredToday();

        new TransactionTemplate(transactionManager)
                .executeWithoutResult(status -> eventPublisher.publishEvent(sampleAnswerEvent()));

        // Коммит издающей TX → AFTER_COMMIT-слушатель отработал синхронно после коммита.
        assertThat(questionsAnsweredToday()).isEqualTo(before + 1);
    }
}
