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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
        controller = new InterviewMvcController(
                interviewPageMvcService,
                interviewFlowMvcService,
                adminMaintenanceService);
    }

    @Test
    void indexDelegatesToPageService() {
        when(interviewPageMvcService.index(
                "java", "core", true, false, true, false, true, 77L, null, "EXAM", session, model))
                .thenReturn("focus");

        String indexView = controller.index(
                "java", "core", true, false, true, false, true, 77L, null, "EXAM", session, model);

        assertThat(indexView).isEqualTo("focus");
        verify(interviewPageMvcService)
                .index("java", "core", true, false, true, false, true, 77L, null, "EXAM", session, model);
    }

    @Test
    void legacyTrainingRouteRedirectsToCanonicalRootWithWhitelistedParamsOnly() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("topic", "java");
        request.setParameter("mode", "EXAM");
        // Пустые значения не переносим — «?topic=» это мусор в адресе, а не фильтр.
        request.setParameter("group", "  ");
        // Не-whitelist параметры отбрасываем: слепой перенос чужого query с
        // легаси-адреса на канонический — это open-redirect-подобный вектор.
        request.setParameter("utm_source", "spam");

        String view = controller.training(request);

        assertThat(view).isEqualTo("redirect:/?topic=java&mode=EXAM");
        verifyNoInteractions(interviewPageMvcService);
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
