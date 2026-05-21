package com.cheatsheet.quiz.service.imports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record McqSeed(
        @JsonProperty("topic_slug") String topicSlug,
        List<McqSeedQuestion> questions
) {}
