package com.cheatsheet.quiz.config;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт: приложение настроено на graceful shutdown с осмысленным
 * per-phase timeout. Цикл #44 включил это через application.yml — этот
 * тест ловит регрессию, если кто-то снимет server.shutdown=graceful
 * или сильно уменьшит spring.lifecycle.timeout-per-shutdown-phase.
 */
@SpringBootTest
@ActiveProfiles("test")
class GracefulShutdownConfigTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    Environment environment;

    @Autowired
    ConfigurableServletWebServerFactory webServerFactory;

    @Test
    void serverShutdownIsGraceful() {
        assertThat(environment.getProperty("server.shutdown"))
                .isEqualToIgnoringCase("graceful");
    }

    @Test
    void perPhaseTimeoutIsAtLeastTenSeconds() {
        // Меньше 10s не даёт активным запросам реалистично завершиться;
        // дефолт K8s grace = 30s, держим 10s как нижнюю безопасную планку.
        Duration timeout = environment.getProperty(
                "spring.lifecycle.timeout-per-shutdown-phase", Duration.class, Duration.ZERO);
        assertThat(timeout).isGreaterThanOrEqualTo(Duration.ofSeconds(10));
    }

    @Test
    void webServerFactoryShutdownIsGraceful() {
        // Проверка через WebServerFactory — то, на что реально опирается Tomcat.
        // Поле shutdown в factory выставляется из server.shutdown property.
        assertThat(webServerFactory).isNotNull();
        // Read it back via reflection-friendly approach: factory has setShutdown(),
        // but getShutdown() is also exposed on the standard impl. Cast is safe
        // because ConfigurableServletWebServerFactory's impls all expose Shutdown.
        Shutdown configured = (Shutdown) org.springframework.beans.BeanWrapper.class
                .cast(new org.springframework.beans.BeanWrapperImpl(webServerFactory))
                .getPropertyValue("shutdown");
        assertThat(configured).isEqualTo(Shutdown.GRACEFUL);
    }
}
