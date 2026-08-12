package com.cheatsheet.quiz.domain;

import java.util.List;

/**
 * Карта темы — по клетке на каждый вопрос темы.
 *
 * <h2>Зачем она</h2>
 * Замер нижней пустоты первого экрана вопроса (Chromium и WebKit): 68px (8%) на 1440×900,
 * 150px (15%) на 1512×982, 434px (36%) на 1920×1200 и 674px (47%) на 2560×1440. На больших
 * экранах почти половина окна не занята ничем. Карта закрывает часть этой площади не
 * декором, а ориентиром: сколько вопросов темы уже закрыто, сколько ждёт повторения,
 * сколько не тронуто.
 *
 * <p>Счётчики считаются здесь, а не в шаблоне: подсчёт в разметке — это логика, которую
 * не проверить тестом, и она разъедется с легендой при первом же новом состоянии.</p>
 *
 * @param cells состояния вопросов в порядке банка
 */
public record TopicMap(List<QuestionProgress> cells) {

    /** Состояние одного вопроса на карте. */
    public enum QuestionProgress {
        /** Ни одной попытки. */
        FRESH,
        /** Есть попытки, срок повторения ещё не наступил. */
        IN_PROGRESS,
        /** Срок повторения наступил. */
        DUE,
        /** Повторений достаточно — вопрос освоен. */
        LEARNED
    }

    /** Пустая карта: темы нет или в ней нет вопросов. */
    public static final TopicMap EMPTY = new TopicMap(List.of());

    public TopicMap {
        cells = cells == null ? List.of() : List.copyOf(cells);
    }

    public int total() {
        return cells.size();
    }

    public int learned() {
        return count(QuestionProgress.LEARNED);
    }

    public int due() {
        return count(QuestionProgress.DUE);
    }

    public int fresh() {
        return count(QuestionProgress.FRESH);
    }

    /** Начатые, но ещё не освоенные и не подошедшие к сроку. */
    public int inProgress() {
        return count(QuestionProgress.IN_PROGRESS);
    }

    private int count(QuestionProgress state) {
        int n = 0;
        for (QuestionProgress cell : cells) {
            if (cell == state) {
                n++;
            }
        }
        return n;
    }
}
