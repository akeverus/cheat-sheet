/**
 * Подготовка к собеседованию (Interview Prep) — приложение для тестирования перед интервью.
 *
 * <p>Основные компоненты:</p>
 * <ul>
 *   <li>{@link com.cheatsheet.quiz.InterviewApplication} — точка входа;</li>
 *   <li>{@link com.cheatsheet.quiz.feature.interview.controller.InterviewMvcController} — веб-интерфейс тестирования (Thymeleaf);</li>
 *   <li>{@link com.cheatsheet.quiz.feature.interview.controller.InterviewApiController} — REST API тестирования;</li>
 *   <li>{@link com.cheatsheet.quiz.domain} — доменная модель (вопросы, варианты, сессии);</li>
 *   <li>{@link com.cheatsheet.quiz.service} — бизнес-логика (тестирование, AI, импорт, подсказки);</li>
 *   <li>{@link com.cheatsheet.quiz.persistence} — доступ к БД (SQLite/PostgreSQL).</li>
 * </ul>
 */
package com.cheatsheet.quiz;
