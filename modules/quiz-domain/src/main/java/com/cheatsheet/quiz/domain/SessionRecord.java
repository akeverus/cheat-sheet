package com.cheatsheet.quiz.domain;

import java.time.Duration;
import java.time.Instant;

/**
 * Запись о завершённой сессии — то, что осталось от неё в истории.
 *
 * <h2>Зачем она есть</h2>
 * Сводка сессии жила только в {@code HttpSession}: экран итогов читал её один раз и терял
 * при следующем старте, перезагрузке или закрытии вкладки. Вопрос «сколько я занимался на
 * прошлой неделе и как шёл» в продукте не имел ответа нигде — ни в аналитике, ни в итогах.
 *
 * <p>Хранятся ИТОГИ, а не ответы: подробности попыток уже лежат в {@code attempt} и
 * {@code review_state}. Здесь то, что оттуда не восстановить, — границы самой сессии.</p>
 *
 * @param id             идентификатор записи
 * @param finishedAt     момент завершения
 * @param mode           режим, в котором шла сессия
 * @param totalQuestions запланированный размер сессии
 * @param correctCount   верных ответов
 * @param wrongCount     неверных ответов
 * @param unknownCount   ответов «не знаю»
 * @param duration       длительность
 * @param sessionXp      опыт, начисленный за сессию
 */
public record SessionRecord(long id, Instant finishedAt, InterviewMode mode,
                            int totalQuestions, int correctCount, int wrongCount,
                            int unknownCount, Duration duration, long sessionXp) {

    public SessionRecord {
        if (totalQuestions < 0 || correctCount < 0 || wrongCount < 0 || unknownCount < 0 || sessionXp < 0) {
            throw new IllegalArgumentException("Счётчики сессии не могут быть отрицательными");
        }
    }

    /** Сколько вопросов реально получили ответ. */
    public int answeredCount() {
        return correctCount + wrongCount + unknownCount;
    }

    /**
     * Точность сессии в процентах, {@code -1} — если отвечать было не на что.
     *
     * <p>Знаменатель — ОТВЕЧЕННЫЕ вопросы, а не запланированный размер: та же
     * арифметика, что у {@link SessionSummary#getAccuracy()}. Иначе брошенная на
     * середине сессия выглядела бы вдвое хуже, чем была.</p>
     */
    public double accuracy() {
        int answered = answeredCount();
        return answered == 0 ? -1 : (correctCount * 100.0) / answered;
    }

    /** Есть ли что показывать в колонке точности. */
    public boolean hasAccuracy() {
        return answeredCount() > 0;
    }

    /**
     * Длительность в компактной форме — ТЕМ ЖЕ правилом, что на экране итогов
     * ({@link SessionSummary#getCompactDuration()}). Считать её в шаблоне значило бы
     * завести второе правило форматирования одной величины: журнал и итоги показывали
     * бы одно занятие по-разному.
     */
    public String compactDuration() {
        if (duration == null) {
            return "—";
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return hours > 0
                ? String.format("%d:%02d:%02d", hours, minutes, seconds)
                : String.format("%d:%02d", minutes, seconds);
    }
}
