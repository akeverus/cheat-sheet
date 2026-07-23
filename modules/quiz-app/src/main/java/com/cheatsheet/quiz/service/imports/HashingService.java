package com.cheatsheet.quiz.service.imports;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Сервис для вычисления SHA-256 хешей.
 *
 * <p>Используется для обнаружения изменений контента вопросов при реимпорте:
 * хеш (questionText + answerMarkdown) сравнивается с сохранённым в БД.</p>
 *
 * @see QuestionImportService
 */
@Component
public class HashingService {

    /** Алгоритм хеширования. */
    private static final String ALGORITHM = "SHA-256";

    /**
     * Вычисляет SHA-256 хеш строки.
     *
     * @param value строка для хеширования
     * @return hex-представление хеша (64 символа)
     * @throws IllegalStateException если алгоритм недоступен (теоретически невозможно)
     */
    public String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // Единственное checked-исключение getInstance. Широкий catch (Exception)
            // прежде глотал и unchecked (напр. NPE при value==null), пряча их за
            // вводящим в заблуждение «алгоритм недоступен» — теперь они всплывают честно.
            throw new IllegalStateException("Не удалось вычислить хеш: " + ALGORITHM, e);
        }
    }
}
