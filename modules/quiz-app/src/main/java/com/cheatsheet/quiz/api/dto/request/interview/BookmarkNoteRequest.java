package com.cheatsheet.quiz.api.dto.request.interview;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Параметры сохранения заметки к закладке (POST /api/bookmark/note).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class BookmarkNoteRequest {

    @Positive(message = "questionId must be greater than 0")
    private long questionId;

    @Size(max = 2000, message = "note must be at most 2000 characters")
    private String note;
}
