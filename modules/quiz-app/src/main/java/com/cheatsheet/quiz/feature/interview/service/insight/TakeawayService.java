package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис генерации и кэширования Key Takeaway — главного вывода для запоминания.
 *
 * <p>Takeaway генерируется один раз через AI и сохраняется в колонку {@code questions.takeaway}.</p>
 * <p>AI иногда генерирует мета-комментарии про интервью вместо технического вывода —
 * такие takeaway автоматически перегенерируются.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TakeawayService {

    QuestionRepository questionRepository;
    AiQuestionClient aiQuestionClient;

    /**
     * Возвращает takeaway для вопроса. Если ещё не сгенерирован или содержит мета-паттерн —
     * генерирует через AI и сохраняет.
     *
     * @param question вопрос
     * @return текст takeaway или пустой Optional
     */
    public Optional<String> getOrGenerate(Question question) {
        String cached = question.takeaway();
        if (cached != null && !cached.isBlank() && !MetaPatternFilter.containsMetaPattern(cached)) {
            return Optional.of(cached);
        }

        boolean needsRegeneration = cached != null && !cached.isBlank() && MetaPatternFilter.containsMetaPattern(cached);

        try {
            Optional<String> takeaway = aiQuestionClient.generateTakeaway(
                    question.questionText(), question.answerMarkdown());

            if (takeaway.isPresent() && !MetaPatternFilter.containsMetaPattern(takeaway.get())) {
                questionRepository.updateTakeaway(question.id(), takeaway.get());
                log.info("Takeaway сохранён для вопроса {} ({})", question.id(), question.slug());
                return takeaway;
            }

            if (needsRegeneration) {
                questionRepository.updateTakeaway(question.id(), "");
                log.info("Takeaway очищен (мета-паттерн) для вопроса {} ({})", question.id(), question.slug());
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Ошибка генерации takeaway для вопроса {}: {}", question.id(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Возвращает takeaway по ID вопроса.
     *
     * @param questionId ID вопроса
     * @return текст takeaway или пустой Optional
     */
    public Optional<String> getOrGenerate(long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.flatMap(this::getOrGenerate);
    }
}
