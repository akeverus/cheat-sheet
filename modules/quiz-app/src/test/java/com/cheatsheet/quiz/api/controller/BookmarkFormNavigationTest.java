package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.feature.admin.service.AdminMaintenanceService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewMvcController;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewFlowMvcService;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewPageMvcService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Exercises HTTP form binding and the actual redirect query/fragment composition. */
class BookmarkFormNavigationTest {
    private final InterviewPageMvcService pages = mock(InterviewPageMvcService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new InterviewMvcController(
            pages, mock(InterviewFlowMvcService.class), mock(AdminMaintenanceService.class))).build();

    @Test
    void savingNotePreservesFiltersAndQuestionAnchorWithoutExposingTheNote() throws Exception {
        when(pages.saveBookmarkNote(eq(5047L), eq("Private draft"), any(RedirectAttributes.class)))
                .thenReturn("redirect:/bookmarks#q-5047");
        mvc.perform(post("/bookmarks/note").param("questionId", "5047").param("note", "Private draft")
                        .param("returnQuery", "GraphQL query").param("returnGroup", "spring")
                        .param("returnNote", "yes").param("returnSort", "old")
                        .param("returnTo", "https://example.com/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookmarks?q=GraphQL+query&group=spring&note=yes&sort=old#q-5047"));
    }

    @Test
    void removingBookmarkPreservesTheFilteredCollection() throws Exception {
        when(pages.removeBookmark(eq(5047L), any(RedirectAttributes.class)))
                .thenReturn("redirect:/bookmarks");
        mvc.perform(post("/bookmarks/remove").param("questionId", "5047")
                        .param("returnQuery", "GraphQL").param("returnNote", "no")
                        .param("returnSort", "topic"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookmarks?q=GraphQL&note=no&sort=topic"));
    }

    @Test
    void missingOrBlankReturnFiltersKeepTheExistingCanonicalRoute() throws Exception {
        when(pages.removeBookmark(eq(5047L), any(RedirectAttributes.class)))
                .thenReturn("redirect:/bookmarks");
        mvc.perform(post("/bookmarks/remove").param("questionId", "5047").param("returnQuery", " ")
                        .param("returnTo", "https://example.com/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookmarks"));
    }
}
