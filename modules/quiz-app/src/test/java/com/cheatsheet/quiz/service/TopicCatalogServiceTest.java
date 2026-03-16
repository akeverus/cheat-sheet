package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
