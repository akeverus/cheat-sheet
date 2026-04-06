package com.cheatsheet.quiz.service.ai.option;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
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

    public AIQuestionService(
            AnswerOptionRepository answerOptionRepository,
            AiQuestionClient aiQuestionClient,
            OptionCache optionCache,
            Striped<Lock> questionLocks,
            TransactionTemplate transactionTemplate
    ) {
        this.answerOptionRepository = answerOptionRepository;
        this.aiQuestionClient = aiQuestionClient;
        this.optionCache = optionCache;
        this.transactionTemplate = transactionTemplate;
        this.questionLocks = questionLocks;
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

            String prompt = QuestionPromptBuilderForOptions.build(question);
            String rawJson = aiQuestionClient.generateStructuredJson(prompt)
                    .orElseThrow(() -> new AiGenerationException(
                            "AI не вернул валидный JSON с вариантами ответа для вопроса id=" + question.id()));
            List<AnswerOptionCreate> created = QuestionResponseMapper.mapToCreates(
                    QuestionResponseMapper.parseGeneratedOptions(rawJson),
                    aiQuestionClient.sourceId()
            );
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
