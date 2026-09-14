package com.cheatsheet.quiz.config.web;

import java.util.List;

/**
 * Первый уровень навигации — один список на обе поверхности.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Разделы были записаны ДВАЖДЫ вручную: в {@code fragments/sidebar.html} и в
 * {@code fragments/mobile-nav.html}. Списки разошлись, и расхождение пережило уже одну
 * починку. Замер до этого класса: рельс — шесть пунктов, нижняя полоса — пять; не хватало
 * каталога банка. То есть раздел, на который ведёт «Темы», на телефоне был недостижим ни
 * одной ссылкой, а {@code topics.html} при этом передавал в нижнюю полосу
 * {@code active='topics'} — значение, которого тот фрагмент не знал, так что подсветка
 * молча не срабатывала.
 *
 * <p>Расходились и иконки одного раздела: рельс печатал {@code #training}, полоса —
 * {@code #home}. Один раздел с двумя глифами — то же расхождение, только незаметнее.
 *
 * <h2>Почему enum, а не «поправить второй список»</h2>
 * Причина дефекта — не опечатка, а ДВА источника. Пока их два, третья починка так же
 * протухнет. Здесь тот же приём, которым в проекте закреплены подписи режимов
 * ({@code ModeUtils}): единственное место, где перечислен первый уровень, и гейт, который
 * запрещает перечислять его где-то ещё.
 *
 * <p>Порядок объявления = порядок в обеих навигациях: хаб дня (Сегодня), работа
 * (Тренировка), материал (Темы), измерение (Прогресс), собранное (Закладки).
 * Настройки — не раздел первого уровня: вход в них стоит в футере рельса
 * ({@code side-action}), рядом с темой и выходом.
 *
 * <h2>IA Wave 1 — хаб и тренировка разведены</h2>
 * Раньше {@code /} был и «домом», и экраном вопроса. Хаб «Сегодня» занимает
 * канонический {@code /}; фокус-тренировка живёт на {@code /practice}. Легаси
 * {@code /training} редиректит на {@code /practice}.
 *
 * <h2>Первый уровень — РАЗДЕЛЫ, а не режимы</h2>
 * Здесь стоял пункт «Ошибки» → {@code /review}. Разделом он не был: {@code /review} и
 * {@code /practice} рендерят один шаблон {@code focus-training.html}, и вся разница
 * между ними — {@code ReviewModeService}, который форсит один флаг фильтра
 * ({@code onlyWrong=true}). То же отбор человек уже мог включить чипом «Ошибки» в
 * конструкторе сессии.
 *
 * <p>Разбор ошибок никуда не делся: вход стоит в тихом статусе оболочки и появляется
 * тогда, когда ошибки есть, — с их числом. Маршрут {@code /review} остаётся рабочим
 * адресом (сохранённые ссылки не ломаются), а активным разделом на нём подсвечивается
 * «Тренировка»: раздел один и тот же, режим внутри него другой.
 */
public enum NavSection {

    HOME("home", "/", "Сегодня", "crest", null),
    FOCUS("focus", "/practice", "Тренировка", "training", null),
    TOPICS("topics", "/topics", "Темы", "topics", "Каталог банка вопросов"),
    ANALYTICS("analytics", "/stats", "Прогресс", "analytics", null),
    BOOKMARKS("bookmarks", "/bookmarks", "Закладки", "bookmark", null);

    private static final List<NavSection> ALL = List.of(values());

    private final String id;
    private final String path;
    private final String label;
    private final String icon;
    private final String hint;

    NavSection(String id, String path, String label, String icon, String hint) {
        this.id = id;
        this.path = path;
        this.label = label;
        this.icon = icon;
        this.hint = hint;
    }

    /** Значение, которое страницы передают фрагментам как {@code active}. */
    public String id() {
        return id;
    }

    /** Адрес раздела. */
    public String path() {
        return path;
    }

    /** Подпись пункта — одна и та же в рельсе и в нижней полосе. */
    public String label() {
        return label;
    }

    /** Идентификатор глифа в пиксельном спрайте (без решётки). */
    public String icon() {
        return icon;
    }

    /** Уточнение для {@code title} там, где подпись короче назначения; иначе {@code null}. */
    public String hint() {
        return hint;
    }

    /** Первый уровень целиком, в порядке объявления. */
    public static List<NavSection> all() {
        return ALL;
    }
}
