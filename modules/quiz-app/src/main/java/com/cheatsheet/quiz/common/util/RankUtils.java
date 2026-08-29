package com.cheatsheet.quiz.common.util;

import com.cheatsheet.quiz.domain.GuildRank;
import org.springframework.stereotype.Component;

/**
 * Знак ступени прогрессии — имя символа в спрайте (fragments/icons.html).
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

    /**
     * Ступень в витрине пути.
     *
     * @param title    название ступени
     * @param glyph    имя герба в спрайте
     * @param fromLevel уровень, с которого ступень начинается
     * @param toLevel  последний уровень ступени; {@code null} у последней — «и выше»
     * @param current  ступень, на которой человек стоит сейчас
     */
    public record RankStep(String title, String glyph, int fromLevel, Integer toLevel, boolean current) {

        /** Подпись диапазона: «уровни 10–14» либо «уровень 30 и выше». */
        public String levels() {
            if (toLevel == null) {
                return "уровень " + fromLevel + " и выше";
            }
            if (toLevel == fromLevel) {
                return "уровень " + fromLevel;
            }
            return "уровни " + fromLevel + "–" + toLevel;
        }
    }

    /**
     * Вся лестница ступеней снизу вверх, с отметкой текущей.
     *
     * <p>Диапазон уровней НЕ хранится в enum и не пишется руками: он выводится из порогов
     * соседних ступеней. Иначе список порогов и список подписей разъехались бы при первой
     * же правке лестницы — ровно так расходились подписи режимов до {@link ModeUtils}.</p>
     */
    public java.util.List<RankStep> ladder(GuildRank current) {
        java.util.List<GuildRank> all = GuildRank.all();
        java.util.List<RankStep> steps = new java.util.ArrayList<>(all.size());
        for (int i = 0; i < all.size(); i++) {
            GuildRank rank = all.get(i);
            Integer to = i + 1 < all.size() ? all.get(i + 1).minLevel() - 1 : null;
            steps.add(new RankStep(rank.title(), glyph(rank), rank.minLevel(), to, rank == current));
        }
        return steps;
    }

    /** Имя символа в спрайте для ступени. */
    public String glyph(GuildRank rank) {
        if (rank == null) {
            return "rank-apprentice";
        }
        return switch (rank) {
            case APPRENTICE -> "rank-apprentice";
            case JOURNEYMAN -> "rank-journeyman";
            case CRAFTSMAN -> "rank-craftsman";
            case MASTER -> "rank-master";
            case SENIOR_MASTER -> "rank-senior-master";
            case KEEPER -> "rank-keeper";
            case MAGISTER -> "rank-magister";
        };
    }
}
