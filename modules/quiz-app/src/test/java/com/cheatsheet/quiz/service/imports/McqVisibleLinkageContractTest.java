package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Контракт видимой связи MCQ со шпаргалкой (ROUND-10, LINKAGE-FIRST).
 *
 * <p>Пользователь в UI видит заголовок {@code ## Q<N>} из markdown-шпаргалки плюс варианты
 * A/B/C/D из JSON-сида. Поле {@code question_text} ему не показывается — оно служебное.
 * Отсюда два инварианта, которые здесь и закреплены:
 *
 * <ol>
 *   <li>{@code question_text} не попадает ни в один персистентный текст варианта —
 *       значит, проектировать варианты вокруг него нельзя, настоящий stem находится в markdown;</li>
 *   <li>каждый {@code q_number} сида существует как {@code ## Q<N>} в своей шпаргалке —
 *       иначе вариант привяжется не к тому вопросу или потеряется.</li>
 * </ol>
 *
 * <p>Тест намеренно фиксирует механику, а не качество: семантическая связь варианта с материалом
 * {@code Q<N>} проверяется ревью (см. {@code docs/mcq-quality/ROUND-10-LINKAGE-FIRST.md}), а не JUnit.
 */
class McqVisibleLinkageContractTest {

    private static final Pattern Q_HEADING = Pattern.compile("^##\\s+Q(\\d+)\\.", Pattern.MULTILINE);

    @Test
    void questionTextIsNeverPersistedSoItCannotBeTheVisibleStem() throws IOException {
        QuestionRepository questionRepository = mock(QuestionRepository.class);
        AnswerOptionRepository answerOptionRepository = mock(AnswerOptionRepository.class);
        McqJsonLoader loader = new McqJsonLoader(
                new ObjectMapper(), questionRepository, answerOptionRepository, new SimpleMeterRegistry());
        when(questionRepository.findIdByTopicAndQuestionNumber("test/sample-interview", 1))
                .thenReturn(Optional.of(42L));

        String questionText;
        try (var in = getClass().getResourceAsStream("/seed/mcq/test/sample-interview.json")) {
            JsonNode tree = new ObjectMapper().readTree(in);
            questionText = tree.path("questions").get(0).path("blocks").get(0).path("question_text").asText();
        }
        assertThat(questionText).as("фикстура обязана содержать непустой question_text").isNotBlank();

        loader.loadForTopic("test", "sample-interview");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnswerOptionCreate>> captor = ArgumentCaptor.forClass(List.class);
        verify(answerOptionRepository).insertAll(eq(42L), captor.capture());

        assertThat(captor.getValue())
                .as("question_text не должен попадать в БД: пользователь его не видит, "
                        + "поэтому он не является stem'ом и не может оправдывать вариант")
                .allSatisfy(create -> {
                    assertThat(create.optionText()).doesNotContain(questionText);
                    assertThat(create.explanation()).doesNotContain(questionText);
                });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("productionSeeds")
    void everySeedQuestionNumberExistsInItsCheatsheet(String name, Path seed, Path cheatsheet) throws IOException {
        assertThat(cheatsheet)
                .as("сид %s обязан иметь шпаргалку-источник", name)
                .exists();

        Set<Integer> visible = new LinkedHashSet<>();
        Matcher m = Q_HEADING.matcher(Files.readString(cheatsheet));
        while (m.find()) {
            visible.add(Integer.parseInt(m.group(1)));
        }

        JsonNode tree = new ObjectMapper().readTree(seed.toFile());
        List<Integer> seeded = new ArrayList<>();
        tree.path("questions").forEach(q -> seeded.add(q.path("q_number").asInt()));

        assertThat(seeded)
                .as("каждый q_number сида %s обязан существовать как ## Q<N> в %s — "
                        + "иначе варианты привяжутся не к тому вопросу", name, cheatsheet.getFileName())
                .isSubsetOf(visible);
    }

    static Stream<Arguments> productionSeeds() throws IOException {
        Path root = repoRoot();
        Path seedDir = root.resolve("modules/quiz-app/src/main/resources/seed/mcq");
        Path mdDir = root.resolve("cheatsheets/interview");
        try (Stream<Path> files = Files.walk(seedDir)) {
            return files.filter(p -> p.toString().endsWith(".json"))
                    .sorted()
                    .map(p -> {
                        String rel = seedDir.relativize(p).toString();
                        Path md = mdDir.resolve(rel.replaceFirst("\\.json$", ".md"));
                        return Arguments.of(rel, p, md);
                    })
                    .toList()
                    .stream();
        }
    }

    /** Поднимается вверх от рабочего каталога, пока не найдёт корень репозитория. */
    private static Path repoRoot() {
        Path dir = Paths.get("").toAbsolutePath();
        while (dir != null) {
            if (Files.isDirectory(dir.resolve("cheatsheets/interview"))
                    && Files.isDirectory(dir.resolve("modules/quiz-app"))) {
                return dir;
            }
            dir = dir.getParent();
        }
        throw new UncheckedIOException(new IOException(
                "Не найден корень репозитория (ожидались cheatsheets/interview и modules/quiz-app) "
                        + "выше " + Paths.get("").toAbsolutePath()));
    }
}
