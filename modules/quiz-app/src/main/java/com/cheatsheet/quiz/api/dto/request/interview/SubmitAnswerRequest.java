package com.cheatsheet.quiz.api.dto.request.interview;

import com.cheatsheet.quiz.common.constants.QuizConstants;
import com.cheatsheet.quiz.common.constants.ValidationMessages;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Параметры отправки ответа на вопрос.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class SubmitAnswerRequest {

    @Positive(message = ValidationMessages.QUESTION_ID_POSITIVE)
    private long questionId;

    @Positive(message = "optionId must be greater than 0")
    private long optionId;

    private String topic;
    private String group;
    private Boolean important;
    private Boolean onlyWrong;
    private Boolean shuffle;
    private Boolean ordered;

    @Min(0) @Max(5)
    private Integer confidence;

    /**
     * Клиентский ключ идемпотентности (BE-003): UUID, сгенерированный один раз на
     * вопрос и переотправляемый без изменений при retry после сетевой ошибки.
     * Позволяет серверу дедуплицировать запись попытки. Может отсутствовать
     * (no-JS-фоллбэк формы) — тогда дедупликация по ключу не применяется.
     */
    @Size(max = QuizConstants.CLIENT_ATTEMPT_ID_MAX_LENGTH,
            message = ValidationMessages.CLIENT_ATTEMPT_ID_MAX)
    private String clientAttemptId;

    /**
     * Comma-separated id блоков контента (код/схема/доп), которые пользователь
     * раскрывал перед ответом (телеметрия, хендофф-3). Может отсутствовать.
     */
    @Size(max = QuizConstants.OPENED_CONTENT_BLOCK_IDS_MAX_LENGTH,
            message = ValidationMessages.OPENED_CONTENT_BLOCK_IDS_MAX)
    private String openedContentBlockIds;

    /**
     * Клиентское think-time (мс) от показа вопроса до сабмита ({@code performance.now()}).
     * Сервер валидирует диапазон и при отсутствии падает на серверный замер.
     */
    @Min(0)
    @Max(QuizConstants.RESPONSE_TIME_MS_MAX)
    private Integer responseTimeMs;

    /**
     * Ревизия вопроса, которую видел пользователь (BE-002). При отсутствии —
     * сервер пишет latest; при несовпадении questionId — fallback на latest.
     */
    @Positive(message = ValidationMessages.QUESTION_REVISION_ID_POSITIVE)
    private Long questionRevisionId;
}
