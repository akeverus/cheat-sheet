package com.cheatsheet.quiz.api.mapper.response;

import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionOptionDto;
import com.cheatsheet.quiz.feature.interview.dto.response.session.QuestionResponseDTO;
import com.cheatsheet.quiz.feature.interview.dto.response.session.ReviewDTO;
import com.cheatsheet.quiz.feature.interview.dto.response.session.TrainingSessionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Маппер доменных объектов interview-потока в API DTO.
 */
@Component
public class InterviewDtoMapper {

    /**
     * Конвертирует доменный вопрос в API DTO.
     *
     * @param interviewQuestion доменная модель вопроса
     * @return API DTO вопроса
     */
    public QuestionResponseDTO toQuestionResponse(InterviewQuestion interviewQuestion) {
        List<NextQuestionOptionDto> options = interviewQuestion.options().stream()
                .map(opt -> new NextQuestionOptionDto(opt.id(), opt.optionText()))
                .toList();
        return new QuestionResponseDTO(
                interviewQuestion.question().id(),
                interviewQuestion.question().questionText(),
                interviewQuestion.question().topic(),
                interviewQuestion.question().questionType() == null ? "TEXT" : interviewQuestion.question().questionType().name(),
                interviewQuestion.question().codeSnippet(),
                interviewQuestion.question().diagramMermaid(),
                options
        );
    }

    /**
     * Конвертирует состояние сессии в API DTO.
     *
     * @param session доменная сессия
     * @return API DTO сессии
     */
    public TrainingSessionDTO toTrainingSessionDto(InterviewSession session) {
        return new TrainingSessionDTO(
                session.getIndex(),
                session.getTotal(),
                session.getCorrect(),
                session.getWrong(),
                session.isFinished()
        );
    }

    /**
     * Конвертирует состояние интервального повторения в API DTO.
     *
     * @param reviewState доменная модель review-состояния
     * @return API DTO review-состояния
     */
    public ReviewDTO toReviewDto(ReviewState reviewState) {
        return new ReviewDTO(
                reviewState.repetitions(),
                reviewState.intervalDays(),
                reviewState.easeFactor(),
                reviewState.nextReviewAt(),
                reviewState.correctCount(),
                reviewState.wrongCount()
        );
    }
}
