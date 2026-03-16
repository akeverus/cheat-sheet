package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Сервис генерации пошагового trace выполнения кода для CODE-вопросов.
 *
 * <p>Результат кэшируется в памяти (Caffeine), т.к. используется только при показе ответа.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeTraceService {

    private static final int TRACE_CACHE_MAX_SIZE = 200;
    private static final Duration TRACE_CACHE_TTL = Duration.ofHours(2);

    private final QuestionRepository questionRepository;
    private final AiQuestionClient aiQuestionClient;
    private final Cache<Long, String> traceCache = Caffeine.newBuilder()
            .maximumSize(TRACE_CACHE_MAX_SIZE)
            .expireAfterWrite(TRACE_CACHE_TTL)
            .build();

    /**
     * Возвращает JSON с пошаговым trace кода.
     *
     * @param questionId ID вопроса
     * @return JSON-строка с массивом steps или пустой Optional
     */
    public Optional<String> getOrGenerate(long questionId) {
        String cached = traceCache.getIfPresent(questionId);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<Question> optQuestion = questionRepository.findById(questionId);
        if (optQuestion.isEmpty()) {
            return Optional.empty();
        }

        Question question = optQuestion.get();
        if (question.questionType() != QuestionType.CODE || question.codeSnippet() == null || question.codeSnippet().isBlank()) {
            return Optional.empty();
        }

        try {
            Optional<String> trace = aiQuestionClient.generateCodeTrace(question.questionText(), question.codeSnippet());
            trace.ifPresent(t -> {
                traceCache.put(questionId, t);
                log.info("Code trace сгенерирован для вопроса {}", questionId);
            });
            return trace;
        } catch (Exception e) {
            log.error("Ошибка генерации code trace для вопроса {}: {}", questionId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
