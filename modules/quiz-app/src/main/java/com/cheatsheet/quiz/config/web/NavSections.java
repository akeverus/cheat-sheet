package com.cheatsheet.quiz.config.web;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Доступ шаблонов к первому уровню навигации: {@code ${@nav.sections()}}.
 *
 * <p>Бин, а не {@code T(...)}-ссылка на тип: обращение к типу из шаблона обходит контейнер и
 * не проверяется ничем, а имя бина видно в гейте, который следит, что оба фрагмента читают
 * ОДИН источник (см. {@link NavSection}).
 */
@Component("nav")
public class NavSections {

    /** Разделы первого уровня в порядке объявления. */
    public List<NavSection> sections() {
        return NavSection.all();
    }
}
