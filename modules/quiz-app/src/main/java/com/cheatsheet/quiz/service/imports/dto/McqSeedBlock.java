package com.cheatsheet.quiz.service.imports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record McqSeedBlock(
        @JsonProperty("block_idx") int blockIdx,
        @JsonProperty("question_text") String questionText,
        List<McqSeedOption> options
) {}
