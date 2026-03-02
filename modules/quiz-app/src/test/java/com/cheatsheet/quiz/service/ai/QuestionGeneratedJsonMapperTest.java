package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGeneratedJsonMapperTest {

    private final QuestionGeneratedJsonMapper mapper = new QuestionGeneratedJsonMapper(new ObjectMapper());

    @Test
    void mapParsesValidQuestionJson() {
        Optional<Question> mapped = mapper.map(validQuestionJson(), "java", QuestionType.CODE, Difficulty.HARD);

        assertThat(mapped).isPresent();
        Question question = mapped.orElseThrow();
        assertThat(question.topic()).isEqualTo("java");
        assertThat(question.type()).isEqualTo(QuestionType.CODE);
        assertThat(question.difficulty()).isEqualTo(Difficulty.HARD);
        assertThat(question.options()).hasSize(4);
        assertThat(question.tags()).contains("java", "collections");
    }

    @Test
    void mapReturnsEmptyForInvalidJson() {
        Optional<Question> mapped = mapper.map("{not-valid-json", "java", QuestionType.CONCEPT, Difficulty.MEDIUM);

        assertThat(mapped).isEmpty();
    }

    private String validQuestionJson() {
        return """
                {
                  "questionText":"Почему HashMap может терять производительность при плохом hashCode?",
                  "codeSnippet":"Map<String, String> map = new HashMap<>();",
                  "options":[
                    {"id":"A","text":"Из-за роста числа коллизий и длинных цепочек поиска","correct":true,"explanation":"Плохой hashCode увеличивает коллизии и цену поиска в bucket."},
                    {"id":"B","text":"Потому что HashMap автоматически сортирует ключи","correct":false,"explanation":"HashMap не сортирует ключи как TreeMap."},
                    {"id":"C","text":"Потому что hashCode используется только при удалении","correct":false,"explanation":"hashCode используется при вставке, поиске и удалении."},
                    {"id":"D","text":"Потому что equals заменяет выбор bucket","correct":false,"explanation":"bucket выбирается по hashCode, затем применяется equals."}
                  ],
                  "shortExplanation":"Качество hashCode влияет на распределение ключей по bucket.",
                  "detailedExplanation":"Неравномерное распределение ключей повышает число сравнений equals внутри bucket и ухудшает среднюю стоимость операций.",
                  "commonMistake":"Считать, что equals важен, а hashCode на производительность не влияет.",
                  "tags":["java","collections"]
                }
                """;
    }
}
