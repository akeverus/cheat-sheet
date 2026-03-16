package com.cheatsheet.quiz;

import java.net.URI;
import java.nio.file.Paths;
import org.springframework.test.context.DynamicPropertyRegistry;

/**
 * Общая настройка пути к тестовым вопросам для интеграционных тестов (не зависит от CWD).
 */
public final class TestInterviewPath {

    private TestInterviewPath() {}

    public static void register(DynamicPropertyRegistry registry) {
        java.net.URL resourceUrl = TestInterviewPath.class.getResource(TestResources.TEST_INTERVIEW_PATH);
        if (resourceUrl == null) {
            throw new IllegalStateException("Resource " + TestResources.TEST_INTERVIEW_PATH + " not found on classpath");
        }
        try {
            URI resource = resourceUrl.toURI();
            String path = Paths.get(resource).toAbsolutePath().toString();
            registry.add("app.interviewPath", () -> path);
        } catch (Exception e) {
            throw new RuntimeException("Cannot resolve test-interview path", e);
        }
    }
}
