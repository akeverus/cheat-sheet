package com.cheatsheet.quiz.common.util;

import com.cheatsheet.quiz.domain.GuildRank;
import org.springframework.stereotype.Component;

/**
 * Герб ступени гильдии — имя пиксельного знака в спрайте (fragments/icons.html).
 *
 * <h2>Зачем</h2>
 * Ступеней семь ({@link GuildRank}), а знак у карточки прогрессии был один — щит раздела
 * «Тренировка» из навигации. Замер живой страницы: эмблема 48px на десктопе и 32px на
 * телефоне — самая крупная картинка карточки, и она не отличала «Ученика» от «Магистра»,
 * то есть единственный графический элемент прогрессии о прогрессии молчал. Вдобавок тот
 * же знак стоит в меню, и карточка читалась как повтор пункта навигации.
 *
 * <p>Знак ДУБЛИРУЕТ то, что рядом написано словом («Мастер», «уровень 10»), а не
 * заменяет его: смысл не держится на картинке.
 *
 * <h2>Почему здесь, а не в enum</h2>
 * Тем же путём, что {@link ModeUtils}: имя спрайта — презентационная деталь, домену о
 * ней знать незачем. Полноту держит {@code switch} по enum без {@code default} —
 * добавленная ступень не соберётся, пока ей не нарисуют герб.
 */
@Component("rankUtils")
public class RankUtils {

    /** Имя символа в спрайте для ступени. */
    public String glyph(GuildRank rank) {
        if (rank == null) {
            return "px-rank-apprentice";
        }
        return switch (rank) {
            case APPRENTICE -> "px-rank-apprentice";
            case JOURNEYMAN -> "px-rank-journeyman";
            case CRAFTSMAN -> "px-rank-craftsman";
            case MASTER -> "px-rank-master";
            case SENIOR_MASTER -> "px-rank-senior-master";
            case KEEPER -> "px-rank-keeper";
            case MAGISTER -> "px-rank-magister";
        };
    }
}
