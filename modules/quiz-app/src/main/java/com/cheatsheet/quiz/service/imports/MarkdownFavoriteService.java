package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.common.util.InterviewPathResolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервис двусторонней синхронизации флага «избранное» между БД и markdown-файлами.
 *
 * <p>При переключении избранного в UI добавляет или удаляет маркер {@code (!)}
 * в заголовке вопроса исходного markdown-файла.</p>
 *
 * <p>Формат заголовка: {@code ## Q1. (!) Текст вопроса?}</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkdownFavoriteService {

    private final InterviewPathResolver interviewPathResolver;

    /** Паттерн заголовка вопроса: {@code ## Q<число>[.] <текст>}. */
    private static final Pattern QUESTION_HEADER = Pattern.compile("^(##\\s+Q\\d+\\.?\\s*)(.*)$");

    /**
     * Добавляет или удаляет маркер {@code (!)} в заголовке вопроса в markdown-файле.
     *
     * @param question вопрос с {@code filePath} и {@code slug}
     * @param favorite {@code true} — добавить маркер, {@code false} — убрать
     * @return {@code true} если файл был изменён
     */
    public boolean toggleInFile(Question question, boolean favorite) {
        if (question.filePath() == null || question.slug() == null) {
            log.warn("Невозможно обновить MD: filePath или slug = null для вопроса [{}]", question.id());
            return false;
        }

        Path filePath = resolveFilePath(question.filePath());
        if (!Files.exists(filePath)) {
            log.warn("MD-файл не найден: {}", filePath);
            return false;
        }

        int questionNumber = extractQuestionNumber(question.slug());
        if (questionNumber < 0) {
            log.warn("Не удалось извлечь номер вопроса из slug [{}]", question.slug());
            return false;
        }

        // Используем file locking для предотвращения повреждения данных при конкурентных записях
        try (FileChannel channel = FileChannel.open(filePath,
                StandardOpenOption.READ, StandardOpenOption.WRITE);
             FileLock ignored = channel.lock()) {

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            boolean modified = false;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Matcher m = QUESTION_HEADER.matcher(line.trim());
                if (!m.matches()) continue;

                // Проверяем, что это нужный номер вопроса
                String prefix = m.group(1); // "## Q1. " или "## Q1 "
                if (!prefix.matches(".*Q" + questionNumber + "\\.?\\s*")) continue;

                String rest = m.group(2);
                boolean hasMarker = rest.startsWith(MarkdownQuestionParser.IMPORTANT_MARKER);

                if (favorite && !hasMarker) {
                    // Добавляем (!) в начало текста после номера
                    lines.set(i, prefix + MarkdownQuestionParser.IMPORTANT_MARKER + " " + rest);
                    modified = true;
                    log.info("Добавлен маркер (!) в Q{} файла {}", questionNumber, filePath.getFileName());
                } else if (!favorite && hasMarker) {
                    // Удаляем (!) и лишний пробел
                    String cleaned = rest.substring(MarkdownQuestionParser.IMPORTANT_MARKER.length()).stripLeading();
                    lines.set(i, prefix + cleaned);
                    modified = true;
                    log.info("Удалён маркер (!) из Q{} файла {}", questionNumber, filePath.getFileName());
                }
                break; // Нашли нужный вопрос — выходим
            }

            if (modified) {
                Files.write(filePath, lines, StandardCharsets.UTF_8);
            }
            return modified;
        } catch (IOException e) {
            log.error("Ошибка чтения/записи MD-файла {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    /**
     * Вычисляет полный путь к markdown-файлу на основе {@code interviewPath} и относительного пути вопроса.
     */
    private Path resolveFilePath(String relativeFilePath) {
        return interviewPathResolver.resolve(relativeFilePath);
    }

    /**
     * Извлекает номер вопроса из slug (формат {@code path/file.md#QN}).
     *
     * @return номер вопроса или {@code -1} если не найден
     */
    static int extractQuestionNumber(String slug) {
        if (slug == null) return -1;
        int hashIdx = slug.lastIndexOf("#Q");
        if (hashIdx < 0) return -1;
        try {
            return Integer.parseInt(slug.substring(hashIdx + 2));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
