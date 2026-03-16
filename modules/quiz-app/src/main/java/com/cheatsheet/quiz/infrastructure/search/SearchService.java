package com.cheatsheet.quiz.infrastructure.search;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.infrastructure.render.MarkdownRenderService;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис полнотекстового поиска по вопросам.
 *
 * <p>Использует {@link FullTextSearchRepository} (SQLite FTS5 или PostgreSQL tsvector)
 * затем пакетно загружает вопросы через {@link QuestionRepository#findByIds}
 * (решение проблемы N+1).</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchService {
    FullTextSearchRepository fullTextSearchRepository;
    QuestionRepository questionRepository;
    MarkdownRenderService markdownRenderService;
    AppProperties appProperties;

    /**
     * Выполняет полнотекстовый поиск.
     *
     * @param query пользовательский запрос (произвольный текст)
     * @param limit максимальное количество результатов (ограничивается app.search.max-limit)
     * @return список результатов с ID, текстом вопроса, темой и сниппетом
     */
    public List<SearchItem> search(String query, int limit) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        int cappedLimit = Math.min(Math.max(1, limit), appProperties.getSearch().getMaxLimit());

        List<FullTextSearchRepository.SearchResult> results = fullTextSearchRepository.search(query, cappedLimit);
        if (results.isEmpty()) {
            return List.of();
        }

        // Пакетная загрузка вопросов (вместо N+1 запросов)
        List<Long> ids = results.stream().map(FullTextSearchRepository.SearchResult::questionId).toList();
        Map<Long, Question> questionsById = questionRepository.findByIds(ids).stream()
                .collect(Collectors.toMap(Question::id, Function.identity(), (a, b) -> a));

        List<SearchItem> items = new ArrayList<>();
        for (FullTextSearchRepository.SearchResult result : results) {
            Question question = questionsById.get(result.questionId());
            if (question != null) {
                String answerPreview = markdownRenderService.toPlainTextPreview(question.answerMarkdown());
                items.add(new SearchItem(question.id(), question.questionText(), question.topic(), result.snippet(), answerPreview));
            }
        }
        return items;
    }

    /**
     * Результат поиска для отображения в UI.
     *
     * @param questionId   ID вопроса
     * @param questionText текст вопроса
     * @param topic        тема
     * @param snippet      сниппет с подсвеченными совпадениями (контекст FTS)
     * @param answerPreview plain-text превью ответа без markdown
     */
    @Builder(toBuilder = true)
    public record SearchItem(long questionId, String questionText, String topic, String snippet, String answerPreview) {}
}
