package com.cheatsheet.quiz.feature.question.engine.service;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.question.engine.mapper.QuestionGeneratedJsonMapper;
import com.cheatsheet.quiz.feature.question.engine.prompt.QuestionPromptBuilder;
import com.cheatsheet.quiz.feature.question.engine.prompt.QuestionTopicNormalizer;
import com.cheatsheet.quiz.feature.interview.service.topic.AdaptiveDifficultyService;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionGenerationService {
    private final AiQuestionClient aiQuestionClient;
    private final QuestionGeneratedJsonMapper questionGeneratedJsonMapper;
    private final QuestionTopicNormalizer questionTopicNormalizer;
    private final AdaptiveDifficultyService adaptiveDifficultyService;
    private final QuestionPromptBuilder questionPromptBuilder;

    /**
     * Генерирует новый вопрос одним вызовом LLM со строгим JSON-контрактом.
     *
     * @param topic тема вопроса
     * @param type  тип вопроса
     * @return вопрос в формате engine-контракта
     */
    public Question generateQuestion(String topic, QuestionType type) {
        Difficulty difficulty = adaptiveDifficultyService.resolveDifficulty(topic);
        String safeTopic = questionTopicNormalizer.normalize(topic);
        QuestionType safeType = type == null ? QuestionType.CONCEPT : type;
        log.info("question_generation_started topic={} type={} difficulty={}", safeTopic, safeType, difficulty);
        String prompt = questionPromptBuilder.buildQuestionPrompt(difficulty, safeType, safeTopic);
        prompt = AiPrompts.withAdaptiveDifficulty(prompt, adaptiveDifficultyService.resolveTopicAccuracy(safeTopic));
        Optional<Question> generated = aiQuestionClient.generateStructuredJson(prompt)
                .flatMap(content -> questionGeneratedJsonMapper.map(content, safeTopic, safeType, difficulty));
        if (generated.isPresent()) {
            log.info("question_generation_succeeded topic={} type={} difficulty={}", safeTopic, safeType, difficulty);
            return generated.get();
        }
        throw new AiGenerationException("QuestionGenerationService: failed to parse question JSON from model response");
    }
}
