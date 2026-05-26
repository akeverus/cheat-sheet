package com.cheatsheet.quiz.feature.export.service;

import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportServiceTest {

    private QuestionStatsRepository statsRepository;
    private ExportService service;

    @BeforeEach
    void setUp() {
        statsRepository = mock(QuestionStatsRepository.class);
        service = new ExportService(new ObjectMapper(), statsRepository);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "plain", "no-special-chars"})
    void escapeCsvLeavesPlainValuesUntouched(String input) {
        if (input == null) {
            assertThat(ExportService.escapeCsv(null)).isEmpty();
            return;
        }
        assertThat(ExportService.escapeCsv(input)).isEqualTo(input);
    }

    @ParameterizedTest
    @CsvSource({
            "'has,comma',     '\"has,comma\"'",
            "'has\nnewline',  '\"has\nnewline\"'"
    })
    void escapeCsvWrapsSpecialCharsInQuotes(String input, String expected) {
        assertThat(ExportService.escapeCsv(input)).isEqualTo(expected);
    }

    @Test
    void escapeCsvDoublesEmbeddedQuotes() {
        assertThat(ExportService.escapeCsv("she said \"hi\""))
                .isEqualTo("\"she said \"\"hi\"\"\"");
    }

    @Test
    void loadProgressRowsDelegatesToRepository() {
        List<ProgressExportRow> rows = List.of(
                new ProgressExportRow("slug-1", "java", 5, 1, 1000L, 3));
        when(statsRepository.findProgressForExport()).thenReturn(rows);

        assertThat(service.loadProgressRows()).isSameAs(rows);
    }

    @Test
    void exportAsJsonProducesPrettyPrintedUtf8() throws JsonProcessingException {
        List<ProgressExportRow> rows = List.of(
                new ProgressExportRow("slug-1", "java", 5, 1, 1000L, 3));

        byte[] bytes = service.exportAsJson(rows);
        String json = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);

        assertThat(json).contains("\"slug\" : \"slug-1\"");
        assertThat(json).contains("\"topic\" : \"java\"");
        assertThat(json).contains("\n"); // pretty-printer обязан вставить переносы
    }

    @Test
    void exportAsCsvIncludesBomHeaderAndDataRow() {
        List<ProgressExportRow> rows = List.of(
                new ProgressExportRow("slug-1", "java", 5, 1, 1000L, 3));

        String csv = service.exportAsCsv(rows);

        assertThat(csv).startsWith("﻿"); // UTF-8 BOM, Excel дружелюбно
        assertThat(csv).contains("slug,topic,correct_count,wrong_count,next_review_at,repetitions");
        assertThat(csv).contains("slug-1,java,5,1,1000,3");
    }

    @Test
    void exportAsCsvEscapesValuesWithCommas() {
        List<ProgressExportRow> rows = List.of(
                new ProgressExportRow("slug-1", "java,kotlin", 1, 0, 0L, 0));

        String csv = service.exportAsCsv(rows);

        assertThat(csv).contains("slug-1,\"java,kotlin\",1,0,0,0");
    }
}
