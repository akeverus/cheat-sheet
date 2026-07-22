/**
 * MVC и REST-контроллеры.
 *
 * <p>Эндпоинты возвращают HTML (Thymeleaf) или JSON. Mutating POST/PUT/PATCH/DELETE
 * защищены CSRF (cookie {@code XSRF-TOKEN} + header {@code X-XSRF-TOKEN}, SPA-friendly).
 * Исключения: {@code /export}, {@code /actuator/**}. Trust boundary: {@code app.security.mode}
 * ({@code local}|{@code multi}); см. {@code SecurityConfig} и {@code SecurityModeStartupGuard}.</p>
 *
 * @see com.cheatsheet.quiz.feature.interview.controller.InterviewMvcController
 * @see com.cheatsheet.quiz.feature.interview.controller.InterviewApiController
 * @see com.cheatsheet.quiz.feature.export.controller.ExportController
 * @see com.cheatsheet.quiz.feature.admin.controller.AdminController
 */
package com.cheatsheet.quiz.api.controller;
