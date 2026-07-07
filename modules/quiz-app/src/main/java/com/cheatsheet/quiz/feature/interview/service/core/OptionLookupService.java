package com.cheatsheet.quiz.feature.interview.service.core;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.google.common.util.concurrent.Striped;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Резолвер вариантов ответа (MCQ) для вопроса.
 *
 * <p>Источник вариантов — JSON-сидеры из {@code seed/mcq/**}, загруженные в БД
 * при старте ({@code McqJsonLoader}). Если для вопроса нет seed-варианта,
 * возвращается пустой список — UI переключается в режим флешкарты.</p>
 *
 * <p>Никаких обращений к AI/LLM в рантайме нет: варианты либо уже в БД/кэше,
 * либо их нет.</p>
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OptionLookupService {

    AnswerOptionRepository answerOptionRepository;
    OptionCache optionCache;
    Striped<Lock> questionLocks;

    public OptionLookupService(
            AnswerOptionRepository answerOptionRepository,
            OptionCache optionCache,
            Striped<Lock> questionLocks
    ) {
        this.answerOptionRepository = answerOptionRepository;
        this.optionCache = optionCache;
        this.questionLocks = questionLocks;
    }

    /**
     * Возвращает варианты ответа для вопроса из кэша/БД (seed-first) или пустой
     * список, если seed-вариантов нет.
     *
     * @param question вопрос
     * @return список вариантов (может быть пустым — тогда режим флешкарты)
     */
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
            // Нет seed-вариантов → флешкарта, без AI.
            return List.of();
        } finally {
            lock.unlock();
        }
    }
}
