package com.cheatsheet.quiz.feature.interview.service.insight;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

/**
 * Сервис прогрессивных AI-подсказок.
 *
 * <p>Генерирует 3 уровня подсказок за один AI-вызов и кэширует их в БД.
 * При повторных запросах возвращает из кэша.</p>
 *
 * <p>Уровни:</p>
 * <ol>
 *   <li>Лёгкий намёк — направление мысли</li>
 *   <li>Конкретнее — сужает область, отсекает неверные варианты</li>
 *   <li>Почти ответ — при наличии знаний ответ станет очевиден</li>
 * </ol>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HintService {

    /** Максимальное количество уровней подсказок. */
    private static final int MAX_LEVEL = 3;

    /** Возвращает максимальный уровень подсказки (для API). */
    public static int getMaxLevel() {
        return MAX_LEVEL;
    }

    HintRepository hintRepository;
    QuestionRepository questionRepository;
    AnswerOptionRepository answerOptionRepository;
    AiQuestionClient aiQuestionClient;
    Clock clock;

    /**
     * Возвращает подсказку для вопроса указанного уровня.
     *
     * <p>Если подсказки ещё не сгенерированы — вызывает AI и сохраняет все 3 уровня в БД.
     * При недоступности AI выбрасывает исключение.</p>
     *
     * @param questionId ID вопроса
     * @param level      уровень подсказки (1–3)
     * @return подсказка или пустой Optional если вопрос не найден
     */
    public Optional<Hint> getHint(long questionId, int level) {
        int clampedLevel = Math.max(1, Math.min(level, MAX_LEVEL));

        // Проверяем кэш в БД
        Optional<Hint> cached = hintRepository.findByQuestionIdAndLevel(questionId, clampedLevel);
        if (cached.isPresent()) {
            log.debug("Подсказка уровня {} для вопроса {} — из кэша", clampedLevel, questionId);
            return cached;
        }

        // Загружаем вопрос
        Optional<Question> optQuestion = questionRepository.findById(questionId);
        if (optQuestion.isEmpty()) {
            return Optional.empty();
        }

        Question question = optQuestion.get();
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(questionId);
        List<String> wrongOptions = options.stream()
                .filter(o -> !o.correct())
                .map(AnswerOption::optionText)
                .toList();

        // Генерируем через AI
        List<String> hintTexts = aiQuestionClient.generateHints(
                question.questionText(), question.answerMarkdown(), wrongOptions
        ).orElse(null);

        if (hintTexts == null || hintTexts.isEmpty()) {
            throw new AiGenerationException("AI не вернул валидные подсказки для вопроса id=" + questionId);
        }
        List<String> normalizedHints = normalizeHints(hintTexts);

        // Сохраняем все уровни подсказок
        long now = clock.instant().getEpochSecond();
        List<Hint> hints = List.of(
                new Hint(0, questionId, 1, normalizedHints.get(0), now),
                new Hint(0, questionId, 2, normalizedHints.get(1), now),
                new Hint(0, questionId, MAX_LEVEL, normalizedHints.get(MAX_LEVEL - 1), now)
        );
        try {
            hintRepository.insertAll(hints);
            log.info("Сохранены {} подсказки для вопроса {}", MAX_LEVEL, questionId);
        } catch (Exception e) {
            log.error("Ошибка сохранения подсказок для вопроса {} — подсказка будет возвращена без сохранения в БД: {}",
                    questionId, e.getMessage(), e);
            // Возвращаем подсказку даже без сохранения
            return Optional.of(hints.get(clampedLevel - 1));
        }

        // Возвращаем запрошенный уровень из БД
        return hintRepository.findByQuestionIdAndLevel(questionId, clampedLevel)
                .or(() -> Optional.of(hints.get(clampedLevel - 1)));
    }

    private static List<String> normalizeHints(List<String> rawHints) {
        List<String> result = rawHints.stream()
                .filter(text -> text != null && !text.isBlank())
                .map(String::trim)
                .limit(MAX_LEVEL)
                .toList();
        if (result.isEmpty()) {
            return List.of("Подумайте о ключевом принципе.", "Сравните варианты по смыслу.", "Проверьте правило в ответе.");
        }
        if (result.size() == MAX_LEVEL) {
            return result;
        }
        List<String> padded = new java.util.ArrayList<>(result);
        String last = result.get(result.size() - 1);
        while (padded.size() < MAX_LEVEL) {
            padded.add(last);
        }
        return padded;
    }
}
