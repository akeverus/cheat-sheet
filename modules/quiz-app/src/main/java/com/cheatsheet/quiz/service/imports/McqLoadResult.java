package com.cheatsheet.quiz.service.imports;

public record McqLoadResult(
        boolean found,
        int optionsInserted,
        int questionsSkipped
) {
    public static McqLoadResult notFound() {
        return new McqLoadResult(false, 0, 0);
    }

    public static McqLoadResult ok(int optionsInserted, int questionsSkipped) {
        return new McqLoadResult(true, optionsInserted, questionsSkipped);
    }
}
