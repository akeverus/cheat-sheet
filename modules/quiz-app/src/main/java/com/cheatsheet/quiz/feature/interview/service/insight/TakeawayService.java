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
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TakeawayService {
    QuestionRepository questionRepository;
    AiQuestionClient aiQuestionClient;

    /**
     * Возвращает takeaway для вопроса. Если ещё не сгенерирован — генерирует и сохраняет.
     *
     * @param question вопрос
     * @return текст takeaway или пустой Optional
     */
    public Optional<String> getOrGenerate(Question question) {
        if (question.takeaway() != null && !question.takeaway().isBlank()) {
            return Optional.of(question.takeaway());
        }

        try {
            Optional<String> takeaway = aiQuestionClient.generateTakeaway(
                    question.questionText(), question.answerMarkdown());

            if (takeaway.isPresent()) {
                questionRepository.updateTakeaway(question.id(), takeaway.get());
                log.info("Takeaway сохранён для вопроса {} ({})", question.id(), question.slug());
                return takeaway;
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
