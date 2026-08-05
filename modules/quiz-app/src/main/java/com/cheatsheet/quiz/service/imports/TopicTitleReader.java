package com.cheatsheet.quiz.service.imports;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Достаёт название темы из frontmatter markdown-файла с вопросами.
 *
 * <p>Заголовок в файлах записан как {@code title: "<раздел>: <название темы>"},
 * например {@code "Вопросы на собеседовании: CockroachDB"} или
 * {@code "System Design: Дизайн Instagram"}. Название темы — часть после первого
 * двоеточия: раздел в UI и так задан навигацией, повторять его в каждом чипе
 * незачем.</p>
 *
 * <p>Читаем только ведущий frontmatter-блок и обрываем чтение на его закрытии —
 * файлы темы весят десятки килобайт, а нужны первые ~15 строк.</p>
 */
@Component
@Slf4j
public class TopicTitleReader {

    private static final String DELIMITER = "---";
    private static final String TITLE_KEY = "title:";

    /**
     * @param file markdown-файл темы
     * @return название темы или {@code null}, если frontmatter отсутствует либо в нём нет {@code title}
     */
    public String readTopicTitle(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String first = reader.readLine();
            if (first == null || !DELIMITER.equals(first.trim())) {
                return null;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (DELIMITER.equals(trimmed)) {
                    return null;
                }
                if (trimmed.startsWith(TITLE_KEY)) {
                    return topicNameFrom(trimmed.substring(TITLE_KEY.length()));
                }
            }
            return null;
        } catch (IOException e) {
            log.warn("Не прочитан frontmatter файла {}: {}", file, e.getMessage());
            return null;
        }
    }

    /** Снимает кавычки и отбрасывает префикс раздела до первого двоеточия. */
    private String topicNameFrom(String rawValue) {
        String value = rawValue.trim();
        if (value.length() >= 2
                && (value.charAt(0) == '"' || value.charAt(0) == '\'')
                && value.charAt(value.length() - 1) == value.charAt(0)) {
            value = value.substring(1, value.length() - 1).trim();
        }
        int colon = value.indexOf(':');
        String name = colon >= 0 ? value.substring(colon + 1).trim() : value;
        return name.isEmpty() ? null : name;
    }
}
