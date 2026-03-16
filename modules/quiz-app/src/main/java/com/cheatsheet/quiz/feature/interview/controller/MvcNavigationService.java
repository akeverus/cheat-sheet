package com.cheatsheet.quiz.feature.interview.controller;

import org.springframework.stereotype.Component;

/**
 * Централизованный provider маршрутов и view-имен для MVC flow.
 *
 * <p>Позволяет убрать строковые литералы навигации из контроллеров
 * и держать единый контракт редиректов/шаблонов в одном месте.</p>
 */
@Component
public class MvcNavigationService {

    /**
     * Основной redirect на фокус-страницу.
     */
    public String focusRedirect() {
        return "redirect:/";
    }

    /**
     * Redirect к странице итогов сессии.
     */
    public String sessionSummaryRedirect() {
        return "redirect:/session-summary";
    }

    /**
     * View фокус-тренировки.
     */
    public String focusView() {
        return "focus-training";
    }

    /**
     * View страницы настроек.
     */
    public String settingsView() {
        return "settings";
    }

    /**
     * View страницы результата ответа.
     */
    public String resultView() {
        return "result";
    }

    /**
     * View страницы статистики.
     */
    public String statsView() {
        return "stats";
    }

    /**
     * View страницы summary завершенной сессии.
     */
    public String sessionSummaryView() {
        return "session-summary";
    }
}
