package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.api.dto.request.interview.StartSessionRequest;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.feature.admin.service.AdminMaintenanceService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewMvcController;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewFlowMvcService;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewPageMvcService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewMvcControllerTest {

    @Mock
    private InterviewPageMvcService interviewPageMvcService;
    @Mock
    private InterviewFlowMvcService interviewFlowMvcService;
    @Mock
    private AdminMaintenanceService adminMaintenanceService;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    private InterviewMvcController controller;

    @BeforeEach
    void setUp() {
        controller = new InterviewMvcController(interviewPageMvcService, interviewFlowMvcService, adminMaintenanceService);
    }

    @Test
    void indexAndTrainingDelegateToSamePageServiceMethod() {
        when(interviewPageMvcService.index("java", "core", true, false, true, false, true, 77L, "EXAM", session, model))
                .thenReturn("focus");

        String indexView = controller.index("java", "core", true, false, true, false, true, 77L, "EXAM", session, model);
        String trainingView = controller.training("java", "core", true, false, true, false, true, 77L, "EXAM", session, model);

        assertThat(indexView).isEqualTo("focus");
        assertThat(trainingView).isEqualTo("focus");
        verify(interviewPageMvcService, times(2))
                .index("java", "core", true, false, true, false, true, 77L, "EXAM", session, model);
    }

    @Test
    void startDelegatesToFlowService() {
        StartSessionRequest request = new StartSessionRequest();
        when(interviewFlowMvcService.start(request, session)).thenReturn("redirect:/");

        String view = controller.start(request, session);

        assertThat(view).isEqualTo("redirect:/");
        verify(interviewFlowMvcService).start(request, session);
    }

    @Test
    void answerDelegatesToFlowService() {
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        when(interviewFlowMvcService.answer(request, session, model)).thenReturn("result");

        String view = controller.answer(request, session, model);

        assertThat(view).isEqualTo("result");
        verify(interviewFlowMvcService).answer(request, session, model);
    }
}
