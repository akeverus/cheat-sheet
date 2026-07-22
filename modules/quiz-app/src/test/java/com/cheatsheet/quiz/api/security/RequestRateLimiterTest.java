package com.cheatsheet.quiz.api.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class RequestRateLimiterTest {

    @Test
    void allowRespectsMaxRequestsWithinWindow() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-01T12:00:00Z"), ZoneOffset.UTC);
        RequestRateLimiter limiter = new RequestRateLimiter(clock);
        Duration window = Duration.ofMinutes(1);

        assertThat(limiter.allow("client-a", 2, window)).isTrue();
        assertThat(limiter.allow("client-a", 2, window)).isTrue();
        assertThat(limiter.allow("client-a", 2, window)).isFalse();
    }

    @Test
    void allowEvictsTimestampsOutsideWindowWhenClockAdvances() {
        MutableClock clock = new MutableClock(Instant.parse("2026-06-01T12:00:00Z"));
        RequestRateLimiter limiter = new RequestRateLimiter(clock);
        Duration window = Duration.ofSeconds(30);

        assertThat(limiter.allow("client-b", 1, window)).isTrue();
        assertThat(limiter.allow("client-b", 1, window)).isFalse();

        clock.advance(Duration.ofSeconds(31));
        assertThat(limiter.allow("client-b", 1, window)).isTrue();
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant start) {
            this.instant = start;
        }

        private void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
