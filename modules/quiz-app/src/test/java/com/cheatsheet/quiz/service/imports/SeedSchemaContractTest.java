package com.cheatsheet.quiz.service.imports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт: каждый JSON-сид в {@code classpath:seed/mcq/**}{@code /*.json} обязан
 * валидироваться по {@code mcq-schema.json}, и его {@code topic_slug} обязан
 * совпадать с базовым именем файла. Использует ту же схему и тот же валидатор,
 * что и {@link McqJsonLoader} в рантайме — single source of truth.
 *
 * <p>Запускается на каждом {@code gradle check}, ловит мердж невалидного
 * сида до того, как он сломает старт приложения.
 */
class SeedSchemaContractTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonSchema SCHEMA = loadSchema();

    static Stream<Arguments> seedFiles() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:seed/mcq/**/*.json");
        return Stream.of(resources)
                .filter(Resource::isReadable)
                .filter(r -> r.getFilename() != null && !r.getFilename().equals("mcq-schema.json"))
                // Контракт распространяется только на production-сиды (src/main/resources).
                // В test/resources лежат намеренно «битые» фикстуры (например wrong-slug.json),
                // их валидировать этим тестом нельзя.
                .filter(r -> !uri(r).contains("/resources/test/")
                        && !uri(r).contains("/test-classes/"))
                .map(r -> Arguments.of(displayName(r), r));
    }

    private static String uri(Resource r) {
        try {
            return r.getURI().toString();
        } catch (IOException e) {
            return "";
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("seedFiles")
    void seedMatchesSchemaAndFilename(String name, Resource resource) throws IOException {
        JsonNode tree;
        try (InputStream in = resource.getInputStream()) {
            tree = MAPPER.readTree(in);
        }
        Set<ValidationMessage> errors = SCHEMA.validate(tree);
        assertThat(errors)
                .as("Schema validation errors in %s", name)
                .isEmpty();

        String fileSlug = resource.getFilename().replaceFirst("\\.json$", "");
        String topicSlug = tree.path("topic_slug").asText();
        assertThat(topicSlug)
                .as("topic_slug in %s must match filename", name)
                .isEqualTo(fileSlug);
    }

    private static JsonSchema loadSchema() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try (InputStream in = resolver.getResource("classpath:seed/mcq-schema.json").getInputStream()) {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            return factory.getSchema(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load mcq-schema.json from test classpath", e);
        }
    }

    private static String displayName(Resource r) {
        try {
            String uri = r.getURI().toString();
            int idx = uri.indexOf("seed/mcq/");
            return idx >= 0 ? uri.substring(idx) : r.getFilename();
        } catch (IOException e) {
            return r.getFilename();
        }
    }
}
