package com.cheatsheet.quiz.common.util;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewPathResolverTest {

    @Test
    void resolvesRelativePathFromParentWhenWorkingDirectoryIsNested(@TempDir Path tempDir) throws Exception {
        Path root = tempDir.resolve("workspace");
        Path moduleDir = root.resolve("modules").resolve("quiz-app");
        Path interviewDir = root.resolve("cheatsheets").resolve("interview");
        Files.createDirectories(moduleDir);
        Files.createDirectories(interviewDir);

        AppProperties props = new AppProperties();
        props.setInterviewPath("cheatsheets/interview");

        String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", moduleDir.toString());
            InterviewPathResolver resolver = new InterviewPathResolver(props);
            assertThat(resolver.getBasePath()).isEqualTo(interviewDir.normalize());
        } finally {
            if (originalUserDir != null) {
                System.setProperty("user.dir", originalUserDir);
            }
        }
    }

    @Test
    void keepsAbsoluteInterviewPathAsIs(@TempDir Path tempDir) throws Exception {
        Path interviewDir = tempDir.resolve("abs").resolve("interview");
        Files.createDirectories(interviewDir);

        AppProperties props = new AppProperties();
        props.setInterviewPath(interviewDir.toString());

        InterviewPathResolver resolver = new InterviewPathResolver(props);
        assertThat(resolver.getBasePath()).isEqualTo(interviewDir.normalize());
    }
}

