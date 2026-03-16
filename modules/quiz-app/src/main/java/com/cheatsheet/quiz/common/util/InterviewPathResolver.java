package com.cheatsheet.quiz.common.util;

import com.cheatsheet.quiz.config.app.AppProperties;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Резолвер базового пути к директории с markdown-файлами вопросов.
 *
 * <p>Объединяет {@code user.dir} и {@code app.interviewPath} в один нормализованный путь
 * и предоставляет {@link #resolve(String)} для получения полного пути к файлу по относительному.</p>
 */
@Component
@Getter
public class InterviewPathResolver {

    private final Path basePath;

    public InterviewPathResolver(AppProperties props) {
        this.basePath = resolveBasePath(props.getInterviewPath(), Path.of(System.getProperty("user.dir", ".")));
    }

    public Path resolve(String relativePath) {
        return basePath.resolve(relativePath).normalize();
    }

    private static Path resolveBasePath(String configuredPath, Path workingDir) {
        Path configured = Path.of(configuredPath);
        if (configured.isAbsolute()) {
            return configured.normalize();
        }
        Path direct = workingDir.resolve(configured).normalize();
        if (Files.exists(direct)) {
            return direct;
        }
        Path cursor = workingDir.getParent();
        while (cursor != null) {
            Path candidate = cursor.resolve(configured).normalize();
            if (Files.exists(candidate)) {
                return candidate;
            }
            cursor = cursor.getParent();
        }
        return direct;
    }
}
