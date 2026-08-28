package com.cheatsheet.quiz.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guardrail для follow-up CS-20260828-002: три Playwright-спека
 * ({@code console-clean}, {@code reflow}, {@code keyboard-quiz}) обязаны быть
 * запущены отдельным job'ом в {@code .github/workflows/ci.yml}, а триггеры
 * {@code push}/{@code pull_request} — покрывать правки {@code e2e/**}, иначе
 * правка спека не запускает свой же гейт.
 */
class CiE2eGateTest {

    private static final String CONSOLE_CLEAN_SPEC = "console-clean.spec.js";
    private static final String REFLOW_SPEC = "reflow.spec.js";
    private static final String KEYBOARD_QUIZ_SPEC = "keyboard-quiz.spec.js";
    private static final String E2E_GLOB = "e2e/**";

    @Test
    void ciWorkflowIncludesConsoleCleanSpec() {
        assertThat(readCiWorkflow()).contains(CONSOLE_CLEAN_SPEC);
    }

    @Test
    void ciWorkflowIncludesReflowSpec() {
        assertThat(readCiWorkflow()).contains(REFLOW_SPEC);
    }

    @Test
    void ciWorkflowIncludesKeyboardQuizSpec() {
        assertThat(readCiWorkflow()).contains(KEYBOARD_QUIZ_SPEC);
    }

    @Test
    void ciWorkflowPushTriggerPathsCoverE2eDirectory() {
        assertThat(extractTriggerPaths(readCiWorkflow(), "push")).contains(E2E_GLOB);
    }

    @Test
    void ciWorkflowPullRequestTriggerPathsCoverE2eDirectory() {
        assertThat(extractTriggerPaths(readCiWorkflow(), "pull_request")).contains(E2E_GLOB);
    }

    private static String readCiWorkflow() {
        Path workflowPath = findRepoRoot().resolve(".github").resolve("workflows").resolve("ci.yml");
        try {
            return Files.readString(workflowPath);
        } catch (IOException e) {
            throw new UncheckedIOException("не удалось прочитать " + workflowPath, e);
        }
    }

    /**
     * Поднимается от {@code user.dir} вверх, пока не найдёт каталог с {@code .github} —
     * так тест находит корень репозитория независимо от рабочей директории раннера.
     */
    private static Path findRepoRoot() {
        Path cursor = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        while (cursor != null) {
            if (Files.isDirectory(cursor.resolve(".github"))) {
                return cursor;
            }
            cursor = cursor.getParent();
        }
        throw new IllegalStateException("не найден каталог .github выше " + System.getProperty("user.dir"));
    }

    /**
     * Вырезает список {@code paths:} внутри триггера {@code triggerKey} (например
     * {@code push} или {@code pull_request}) блока {@code on:} — построчный
     * YAML-скан по отступам, без внешних библиотек.
     */
    private static String extractTriggerPaths(String workflowYaml, String triggerKey) {
        String[] lines = workflowYaml.split("\n", -1);
        StringBuilder section = new StringBuilder();
        boolean insideTrigger = false;
        for (String line : lines) {
            String trimmed = line.stripLeading();
            int indent = line.length() - trimmed.length();
            if (indent == 2 && trimmed.equals(triggerKey + ":")) {
                insideTrigger = true;
                continue;
            }
            if (insideTrigger) {
                boolean isSiblingKey = indent <= 2 && !trimmed.isBlank();
                if (isSiblingKey) {
                    break;
                }
                section.append(line).append('\n');
            }
        }
        return section.toString();
    }
}
