package com.cheatsheet.quiz.service.imports;

import com.networknt.schema.ValidationMessage;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class InvalidMcqSeedException extends RuntimeException {
    public InvalidMcqSeedException(Path path, Set<ValidationMessage> errors) {
        super("Invalid MCQ seed " + path + ": " + errors.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining("; ")));
    }

    public InvalidMcqSeedException(Path path, String message) {
        super("Invalid MCQ seed " + path + ": " + message);
    }
}
