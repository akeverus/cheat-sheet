package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TopicCatalogServiceTest {

    @Test
    void topicsForFilterUsesGroupAndConfiguredOrder() {
        AppProperties props = new AppProperties();
        TopicCatalogService service = new TopicCatalogService(props);
        InterviewFilter filter = new InterviewFilter(null, "languages", false, false, false, true);

        List<String> ordered = service.topicsForFilter(filter, List.of(
                "programming-languages/kotlin/kotlin-interview",
                "frameworks/spring/spring-boot-interview",
                "programming-languages/java/java-core-interview"
        ));

        assertThat(ordered).containsExactly(
                "programming-languages/java/java-core-interview",
                "programming-languages/kotlin/kotlin-interview"
        );
    }

    @Test
    void groupOptionsShowsOnlyGroupsWithTopics() {
        AppProperties props = new AppProperties();
        TopicCatalogService service = new TopicCatalogService(props);

        List<TopicCatalogService.GroupOption> options = service.groupOptions(List.of(
                "programming-languages/java/java-core-interview",
                "jvm/jvm-interview",
                "frameworks/spring/spring-boot-interview"
        ));

        assertThat(options).extracting(TopicCatalogService.GroupOption::id)
                .contains("languages", "jvm", "spring");
        assertThat(options).filteredOn(option -> option.id().equals("languages"))
                .singleElement()
                .extracting(TopicCatalogService.GroupOption::topicCount)
                .isEqualTo(1L);
    }

    /**
     * Каталог разделов складывает банк и прогресс до уровня группы.
     * Проверяется именно суммирование и порядок каталога: карточка с чужими
     * числами выглядит ровно так же, как правильная, и расхождение всплыло бы
     * только при ручной сверке с таблицей ниже на той же странице.
     */
    @Test
    void groupCatalogSumsBankAndProgressPerGroupInCatalogOrder() {
        AppProperties props = new AppProperties();
        TopicCatalogService service = new TopicCatalogService(props);

        List<TopicCatalogService.GroupProgress> catalog = service.groupCatalog(
                // Spring идёт первым во входе, но в каталоге он после languages —
                // порядок обязан задаваться конфигом, а не порядком строк из БД.
                new LinkedHashMap<>(Map.of(
                        "frameworks/spring/spring-boot-interview", 40L,
                        "programming-languages/java/java-core-interview", 30L,
                        "programming-languages/kotlin/kotlin-interview", 20L
                )),
                List.of(
                        new TopicStats("frameworks/spring/spring-boot-interview", 40, 5, 2, 10, 30, 10, 0.5),
                        new TopicStats("programming-languages/java/java-core-interview", 30, 2, 1, 20, 18, 2, 0.7),
                        new TopicStats("programming-languages/kotlin/kotlin-interview", 20, 0, 0, 5, 0, 0, 0.1)
                ));

        assertThat(catalog).extracting(TopicCatalogService.GroupProgress::id)
                .containsExactly("languages", "spring");

        TopicCatalogService.GroupProgress languages = catalog.getFirst();
        assertThat(languages.topicCount()).isEqualTo(2L);
        assertThat(languages.questionCount()).isEqualTo(50L);
        assertThat(languages.learnedCount()).isEqualTo(25L);
        assertThat(languages.dueCount()).isEqualTo(2L);
        assertThat(languages.accuracyPercent()).isEqualTo(90.0);
        assertThat(languages.learnedPercent()).isEqualTo(50.0);
        // icon — id символа спрайта fragments/icons.html из topic-groups.yaml,
        // а не путь к файлу: карточка раздела рисует его через <use href="#...">.
        assertThat(languages.icon()).isEqualTo("section-languages");
    }

    /**
     * Раздел, к которому не притрагивались, обязан быть в каталоге — иначе тот
     * показывает только начатое и не отвечает на вопрос «куда пойти дальше»,
     * ради которого существует. Прогресс при этом нулевой, а точность —
     * {@code null}: 0% означало бы «отвечал и всё неверно».
     */
    @Test
    void groupCatalogCoversUntouchedGroupsFromTheBank() {
        AppProperties props = new AppProperties();
        TopicCatalogService service = new TopicCatalogService(props);

        List<TopicCatalogService.GroupProgress> catalog = service.groupCatalog(
                new LinkedHashMap<>(Map.of(
                        "databases/sql-interview", 12L,
                        "devops/docker-interview", 8L
                )),
                // Прогресс есть только по одному разделу из двух.
                List.of(new TopicStats("databases/sql-interview", 12, 3, 1, 6, 9, 1, 0.4)));

        assertThat(catalog).extracting(TopicCatalogService.GroupProgress::id)
                .containsExactly("databases", "delivery");
        assertThat(catalog).filteredOn(group -> group.id().equals("delivery"))
                .singleElement()
                .satisfies(delivery -> {
                    assertThat(delivery.questionCount()).isEqualTo(8L);
                    assertThat(delivery.learnedCount()).isZero();
                    assertThat(delivery.accuracyPercent()).isNull();
                });
    }

    /**
     * Тема, вопросы которой удалили, оставляет след в review_state. Её прогресс
     * не должен попадать в каталог: «изучено» стало бы больше, чем «всего», и
     * полоса ушла бы за 100% — раздел выглядел бы пройденным сверх банка.
     */
    @Test
    void groupCatalogIgnoresProgressForTopicsMissingFromTheBank() {
        AppProperties props = new AppProperties();
        TopicCatalogService service = new TopicCatalogService(props);

        List<TopicCatalogService.GroupProgress> catalog = service.groupCatalog(
                new LinkedHashMap<>(Map.of("databases/sql-interview", 10L)),
                List.of(
                        new TopicStats("databases/sql-interview", 10, 0, 0, 4, 4, 0, 0.4),
                        // Раздела delivery в банке нет — прогресс по нему сирота.
                        new TopicStats("devops/docker-interview", 50, 0, 0, 50, 50, 0, 0.9)
                ));

        assertThat(catalog).singleElement()
                .satisfies(databases -> {
                    assertThat(databases.id()).isEqualTo("databases");
                    assertThat(databases.learnedCount()).isEqualTo(4L);
                    assertThat(databases.learnedPercent()).isEqualTo(40.0);
                });
    }
}
