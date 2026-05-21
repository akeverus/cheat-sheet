package com.cheatsheet.quiz.service.imports.dto;

import java.util.Map;

public record McqSeedOption(
        int order,
        String label,
        String text,
        boolean correct,
        Map<String, String> sections
) {}
