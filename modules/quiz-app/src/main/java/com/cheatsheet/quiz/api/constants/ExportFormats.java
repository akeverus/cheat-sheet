package com.cheatsheet.quiz.api.constants;

import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Допустимые форматы экспорта прогресса.
 */
@UtilityClass
public class ExportFormats {

    public static final String JSON = "json";
    public static final String CSV = "csv";
    public static final List<String> ALL = List.of(JSON, CSV);
}
