package com.cheatsheet.quiz.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Единственное место, где живёт развилка «отвечать JSON или HTML».
 *
 * <p>Решение принимают четыре независимых участника: {@code GlobalExceptionHandler}
 * и три security-хендлера ({@code AccessDeniedHandler}, {@code AuthenticationEntryPoint},
 * {@code SensitiveEndpointAccessService}). Раньше каждый нёс собственную копию
 * предиката, и копии успели разойтись: одна смотрела только на путь, другая — на
 * явный {@code text/html}, третья считала пустой {@code Accept} признаком браузера.
 * Правила физически не могли остаться согласованными.</p>
 *
 * <p>Развилка разложена на два ортогональных вопроса, потому что смешивать их
 * нельзя: «куда адресован запрос» (свойство пути) и «что клиент готов принять»
 * (свойство заголовка). Хендлеры комбинируют их сами — комбинации у них
 * намеренно разные.</p>
 */
public final class RequestFormat {

    private static final String ANY_MEDIA_TYPE = "*/*";

    private RequestFormat() {
    }

    /**
     * Путь предназначен машине: {@code /api/**} или {@code /export}. Браузер сюда
     * тоже ходит (ссылка на выгрузку), поэтому одного пути для выбора формата мало.
     */
    public static boolean isMachineFacingPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.startsWith("/api/") || uri.startsWith("/export"));
    }

    /**
     * Путь закрыт admin-гейтом. Список шире {@link #isMachineFacingPath}: сюда
     * входит {@code /settings/reset-options} — обычная MVC-форма, не API.
     */
    public static boolean isAdminSensitivePath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.startsWith("/export") || uri.startsWith("/settings/reset-options"));
    }

    /**
     * Клиент явно назвал {@code text/html}. Строгая проверка: молчание не считается
     * согласием. Применяется там, где ошибочный HTML вместо JSON ломает клиента
     * сильнее, чем наоборот.
     */
    public static boolean acceptsHtml(HttpServletRequest request) {
        String accept = accept(request);
        return accept != null && accept.contains(MediaType.TEXT_HTML_VALUE);
    }

    /**
     * Клиент похож на браузер. Мягкая проверка: пустой или отсутствующий
     * {@code Accept} и {@code *&#47;*} без JSON трактуются как «покажи страницу».
     * Применяется на путях, где по умолчанию ожидается человек (форма логина).
     */
    public static boolean prefersHtml(HttpServletRequest request) {
        String accept = accept(request);
        if (accept == null || accept.isBlank()) {
            return true;
        }
        return accept.contains(MediaType.TEXT_HTML_VALUE)
                || (!accept.contains(MediaType.APPLICATION_JSON_VALUE) && accept.contains(ANY_MEDIA_TYPE));
    }

    /**
     * Клиент просит JSON и при этом не готов принять HTML.
     *
     * <p>Проверять только наличие {@code application/json} нельзя: браузерная
     * навигация шлёт {@code text/html,...,*&#47;*}, и любой прокси или расширение,
     * дописывающее JSON в {@code Accept}, показало бы пользователю голый JSON
     * вместо страницы ошибки. Решает отсутствие HTML, а не наличие JSON.</p>
     */
    public static boolean prefersJson(HttpServletRequest request) {
        String accept = accept(request);
        if (accept == null) {
            return false;
        }
        return accept.contains(MediaType.APPLICATION_JSON_VALUE)
                && !accept.contains(MediaType.TEXT_HTML_VALUE);
    }

    private static String accept(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.ACCEPT);
    }
}
