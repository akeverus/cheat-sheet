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
     * Redirect на фокус-тренировку ({@code /practice}).
     */
    public String focusRedirect() {
        return "redirect:/practice";
    }

    /**
     * Redirect на фокус-страницу С ЗАКРЕПЛЁННЫМ вопросом.
     *
     * <p>Обычный {@link #focusRedirect()} возвращает «на экран вопроса», и в сессии
     * этого достаточно: вопрос выбирает сама сессия. Вне сессии (просмотр банка)
     * тот же адрес выдал бы СЛЕДУЮЩИЙ вопрос — то есть человек, записавший заметку,
     * терял бы вопрос, к которому её писал. Закрепление адресом решает оба случая
     * одним путём: в сессии параметр игнорируется (вопрос берётся из неё), вне
     * сессии — возвращает ровно на тот вопрос.
     */
    public String focusQuestionRedirect(long questionId) {
        return "redirect:/practice?questionId=" + questionId;
    }

    /**
     * View хаба «Сегодня» ({@code /}).
     */
    public String homeView() {
        return "home";
    }

    /**
     * Redirect на хаб «Сегодня».
     */
    public String homeRedirect() {
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
     * View поверхности session-start «Собрать сессию» (UXD-001): конструктор
     * выделен из вкладок /settings в собственный маршрут /train.
     */
    public String trainView() {
        return "train";
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
     * View каталога тем — навигация по банку, а не срез аналитики.
     */
    public String topicsView() {
        return "topics";
    }

    /**
     * View страницы закладок и заметок (UI-014).
     */
    public String bookmarksView() {
        return "bookmarks";
    }

    /**
     * View страницы summary завершенной сессии.
     */
    public String sessionSummaryView() {
        return "session-summary";
    }
}
