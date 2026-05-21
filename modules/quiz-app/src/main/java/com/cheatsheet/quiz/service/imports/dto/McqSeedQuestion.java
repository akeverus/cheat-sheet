package com.cheatsheet.quiz.service.imports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record McqSeedQuestion(
        @JsonProperty("q_number") int qNumber,
        List<McqSeedBlock> blocks
) {}
