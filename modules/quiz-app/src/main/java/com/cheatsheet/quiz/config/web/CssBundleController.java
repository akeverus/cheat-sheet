package com.cheatsheet.quiz.config.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * Отдаёт склеенный CSS страницы: {@code /css/bundle/focus.a1b2c3d4.css}.
 *
 * <p>Соседство с {@code static/css/*.css} только по адресу — лента собирается в
 * памяти и мимо цепочки ресурсов Spring, поэтому кэш-заголовки ставятся тут
 * руками. Хеш в имени делает адрес уникальным для содержимого, отсюда годовой
 * {@code immutable}: браузер не переспрашивает файл вообще, а правка CSS меняет
 * имя и приезжает со следующей навигацией (HTML отдаётся {@code no-store}).</p>
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestController
public class CssBundleController {

    private static final MediaType CSS_UTF8 = MediaType.valueOf("text/css;charset=UTF-8");

    private final CssBundleService cssBundle;

    public CssBundleController(CssBundleService cssBundle) {
        this.cssBundle = cssBundle;
    }

    @GetMapping(value = CssBundleService.URL_PREFIX + "{page}.{hash}.css", produces = "text/css")
    public ResponseEntity<String> bundle(@PathVariable String page, @PathVariable String hash) {
        return cssBundle.css(page, hash)
                .map(css -> ResponseEntity.ok()
                        .contentType(CSS_UTF8)
                        .cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
                        .body(css))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
