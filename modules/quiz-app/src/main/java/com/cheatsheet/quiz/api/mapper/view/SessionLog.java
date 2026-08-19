package com.cheatsheet.quiz.api.mapper.view;

import com.cheatsheet.quiz.domain.InterviewSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Ход текущей сессии: что уже отвечено и чем это кончилось.
 *
 * <h2>Зачем</h2>
 * Замер экрана вопроса на широком мониторе (Chromium, 2560×1440, код-челлендж):
 * колонка задания кончается на 624-м пикселе, рабочая — на 1116-м, то есть под
 * заданием остаётся 768×492 пустоты внутри первого экрана. §38 брифа велит сначала
 * спросить, какую пользу человек получит на свободной площади, и только потом
 * что-то туда класть. Польза здесь есть и она уже посчитана: сессия ведёт
 * {@link InterviewSession#getAnswerHistory()} — по записи на каждый ответ. Ни
 * одного нового запроса в БД журнал не стоит.
 *
 * <h2>Порядок и предел</h2>
 * Свежие записи идут ПЕРВЫМИ: журнал читают, чтобы увидеть последние ответы, а не
 * начало марафона. Показываем последние {@code limit} штук, остальное сворачивается
 * в две цифры («раньше: N верных, M ошибок») — на марафоне в 200 вопросов полный
 * список был бы длиннее самой страницы и превратил бы колонку задания в ленту.
 *
 * @param entries       показываемые записи, свежая первой
 * @param hiddenCorrect сколько верных ответов осталось за пределом показа
 * @param hiddenWrong   сколько ошибок осталось за пределом показа
 */
public record SessionLog(List<Entry> entries, int hiddenCorrect, int hiddenWrong) {

    /** Пустой журнал: сессии нет или первый вопрос ещё не отвечен. */
    public static final SessionLog EMPTY = new SessionLog(List.of(), 0, 0);

    /**
     * Одна запись журнала.
     *
     * @param number  номер вопроса в сессии, с единицы — то же число, что в «1/20»
     * @param correct ответ засчитан
     * @param topic   slug темы; человеческое имя даёт {@code TopicUtils#displayName}
     */
    public record Entry(int number, boolean correct, String topic) {
    }

    /**
     * Собирает журнал из истории ответов сессии.
     *
     * @param history история ответов (может быть {@code null})
     * @param limit   сколько последних записей показывать; не больше нуля — журнала нет
     * @return журнал; {@link #EMPTY}, если показывать нечего
     */
    public static SessionLog of(List<InterviewSession.AnswerRecord> history, int limit) {
        if (history == null || history.isEmpty() || limit <= 0) {
            return EMPTY;
        }
        List<Entry> entries = new ArrayList<>();
        int shown = Math.min(limit, history.size());
        for (int i = history.size() - 1; i >= history.size() - shown; i--) {
            InterviewSession.AnswerRecord record = history.get(i);
            entries.add(new Entry(i + 1, record.correct(), record.topic()));
        }
        int hiddenCorrect = 0;
        int hiddenWrong = 0;
        for (int i = 0; i < history.size() - shown; i++) {
            if (history.get(i).correct()) {
                hiddenCorrect++;
            } else {
                hiddenWrong++;
            }
        }
        return new SessionLog(List.copyOf(entries), hiddenCorrect, hiddenWrong);
    }

    /** Есть ли что показывать. Пустой журнал шаблон не рисует вовсе. */
    public boolean present() {
        return !entries.isEmpty();
    }

    /** Остались ли записи за пределом показа. */
    public boolean truncated() {
        return hiddenCorrect + hiddenWrong > 0;
    }
}
