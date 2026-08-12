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
    private static final String DESCRIPTION_KEY = "description:";

    /**
     * @param file markdown-файл темы
     * @return название темы или {@code null}, если frontmatter отсутствует либо в нём нет {@code title}
     */
    /**
     * Описание темы из того же frontmatter — одна строка о том, что вообще входит в тему.
     *
     * <p>Читается тем же проходом и по тем же правилам, что и заголовок: описание —
     * такая же часть исходника темы, как её вопросы, и живёт там же. Отдельного
     * хранилища у него нет по той же причине, что и у названия: банк перечитывается
     * из файлов на каждом старте, и таблица в БД дублировала бы источник правды.</p>
     *
     * @param file markdown-файл темы
     * @return описание или {@code null}, если frontmatter отсутствует либо описания в нём нет
     */
    public String readTopicDescription(Path file) {
        return readValue(file, DESCRIPTION_KEY, false);
    }

    public String readTopicTitle(Path file) {
        return readValue(file, TITLE_KEY, true);
    }

    /**
     * @param dropSectionPrefix для заголовка — отбросить «<раздел>: » слева; описание
     *                          режется по двоеточию не всегда осмысленно, поэтому у него
     *                          префикс не снимается
     */
    private String readValue(Path file, String key, boolean dropSectionPrefix) {
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
                if (trimmed.startsWith(key)) {
                    return valueFrom(trimmed.substring(key.length()), dropSectionPrefix);
                }
            }
            return null;
        } catch (IOException e) {
            log.warn("Не прочитан frontmatter файла {}: {}", file, e.getMessage());
            return null;
        }
    }

    /** Снимает кавычки и, для заголовка, отбрасывает префикс раздела до первого двоеточия. */
    private String valueFrom(String rawValue, boolean dropSectionPrefix) {
        String value = rawValue.trim();
        if (value.length() >= 2
                && (value.charAt(0) == '"' || value.charAt(0) == '\'')
                && value.charAt(value.length() - 1) == value.charAt(0)) {
            value = value.substring(1, value.length() - 1).trim();
        }
        if (dropSectionPrefix) {
            int colon = value.indexOf(':');
            value = colon >= 0 ? value.substring(colon + 1).trim() : value;
        }
        return value.isEmpty() ? null : value;
    }
}
