package com.cheatsheet.quiz.feature.auth.service;

import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Модель обзора банка вопросов для страницы входа.
 *
 * <p>Экран входа стоит перед человеком, который ещё ничего о приложении не
 * знает: до аутентификации у него нет ни прогресса, ни сессии, ни закладок.
 * Единственное, что можно честно показать рядом с формой, — из чего состоит
 * банк вопросов. Данные берутся из того же источника, что и каталог разделов
 * на /stats ({@link TopicCatalogService#groupCatalog}), поэтому цифры на входе
 * и внутри не могут разойтись.
 *
 * <p>Прогрессовые поля {@code GroupProgress} на этой странице не рендерятся:
 * анонимному посетителю они все равны нулю, а «изучено 0 из 1200» — не факт о
 * банке, а артефакт отсутствия аккаунта.
 *
 * <p>Бин поднимается только в {@code app.security.mode=multi} — ровно там, где
 * существует сама страница входа.
 */
@Service
@ConditionalOnProperty(prefix = "app.security", name = "mode", havingValue = "multi")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginPageService {

    QuestionRepository questionRepository;
    TopicCatalogService topicCatalogService;

    /**
     * @return разделы банка с числом тем и вопросов; пустой список, если банк пуст
     *         (тогда страница входа показывает форму без обзора — см. login.html)
     */
    public Overview overview() {
        Map<String, Long> countsByTopic = questionRepository.countQuestionsByTopic();
        List<TopicCatalogService.GroupProgress> sections =
                topicCatalogService.groupCatalog(countsByTopic, List.of());
        long questions = countsByTopic.values().stream()
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
        return new Overview(questions, countsByTopic.size(), sections);
    }

    /**
     * @param questionCount всего вопросов в банке
     * @param topicCount    всего тем в банке
     * @param sections      разделы каталога, уже в порядке каталога
     */
    public record Overview(long questionCount, long topicCount, List<TopicCatalogService.GroupProgress> sections) {

        /** Пустой банк — обзор рисовать не из чего. */
        public boolean isEmpty() {
            return questionCount == 0 || sections.isEmpty();
        }
    }
}
