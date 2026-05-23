package com.cheatsheet.quiz.service.ai.option;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.google.common.util.concurrent.Striped;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.locks.Lock;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AIQuestionService {
    AnswerOptionRepository answerOptionRepository;
    AiQuestionClient aiQuestionClient;
    OptionCache optionCache;
    TransactionTemplate transactionTemplate;
    Striped<Lock> questionLocks;
    AppProperties appProperties;

    public AIQuestionService(
            AnswerOptionRepository answerOptionRepository,
            AiQuestionClient aiQuestionClient,
            OptionCache optionCache,
            Striped<Lock> questionLocks,
            TransactionTemplate transactionTemplate,
            AppProperties appProperties
    ) {
        this.answerOptionRepository = answerOptionRepository;
        this.aiQuestionClient = aiQuestionClient;
        this.optionCache = optionCache;
        this.transactionTemplate = transactionTemplate;
        this.questionLocks = questionLocks;
        this.appProperties = appProperties;
    }

    public List<AnswerOption> getOrCreateOptions(Question question) {
        List<AnswerOption> cached = optionCache.get(question.id());
        if (cached != null) {
            return cached;
        }

        Lock lock = questionLocks.get(question.id());
        lock.lock();
        try {
            List<AnswerOption> existing = answerOptionRepository.findByQuestionId(question.id());
            if (existing != null && !existing.isEmpty()) {
                optionCache.put(question.id(), existing);
                return existing;
            }

            // Seed-first: если для вопроса нет загруженных вариантов и AI-fallback
            // не разрешён (app.ai.fallback-enabled=false по умолчанию), просто
            // возвращаем пусто — UI покажет флешкарту, никаких AI-вызовов в рантайме.
            if (!appProperties.isAiFallbackAllowed()) {
                return List.of();
            }

            GeneratedOptions generated = aiQuestionClient.generateOptions(question.questionText(), question.codeSnippet())
                    .orElseThrow(() -> new AiGenerationException(
                            "AI не вернул валидные варианты ответа для вопроса id=" + question.id()));
            List<AnswerOptionCreate> created = QuestionResponseMapper.mapToCreates(generated, aiQuestionClient.sourceId());
            transactionTemplate.executeWithoutResult(status -> {
                answerOptionRepository.deleteByQuestionId(question.id());
                answerOptionRepository.insertAll(question.id(), created);
            });

            List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.id());
            optionCache.put(question.id(), options);
            return options;
        } finally {
            lock.unlock();
        }
    }
}
