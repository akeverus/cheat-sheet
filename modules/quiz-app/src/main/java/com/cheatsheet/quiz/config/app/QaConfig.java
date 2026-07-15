package com.cheatsheet.quiz.config.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Конфигурация профиля {@code qa} — детерминированное окружение для
 * воспроизводимого browser-QA (визуальная фаза finalization-раунда).
 *
 * <p>Даёт <b>фиксированный</b> {@link Clock} ({@link Primary}, перекрывает
 * {@link InfrastructureConfig#clock()}), чтобы все зависящие от времени расчёты
 * (SM-2 {@code next_review_at}, «сегодня» в статистике, метки времени) были
 * идентичны от прогона к прогону. Детерминированный набор вопросов due/learned/new
 * сеет {@code QaFixtureRunner}, опираясь на этот же Clock.</p>
 *
 * <p>Профиль активируется явно: {@code SPRING_PROFILES_ACTIVE=qa ./gradlew bootRun}.
 * В prod/dev не участвует.</p>
 */
@Configuration
@Profile("qa")
@Slf4j
public class QaConfig {

    /**
     * Фиксированный момент времени QA-окружения (UTC). Настраивается через
     * {@code app.qa.fixed-instant}; дефолт — полдень выбранной даты, чтобы
     * «сегодня» в статистике было устойчивым независимо от таймзоны рендера.
     */
    @Bean
    @Primary
    Clock qaClock(@Value("${app.qa.fixed-instant:2026-07-15T12:00:00Z}") String fixedInstant) {
        Instant instant = Instant.parse(fixedInstant);
        log.warn("QA-профиль: используется ФИКСИРОВАННЫЙ Clock={} (детерминированный browser-QA)", instant);
        return Clock.fixed(instant, ZoneOffset.UTC);
    }
}
