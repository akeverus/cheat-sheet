package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.feature.interview.controller.InterviewMvcController;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Тихий статус в сайдбаре: очередь повторений и счётчик вопросов с ошибками.
 *
 * <p>Появился из аудита «тихий интерфейс» (2026-09-02): очередь и вход в разбор
 * ошибок печатались КРУПНО на экране вопроса — статусная банда из шести чисел
 * конкурировала с самим вопросом, дублируя /stats. Числам оставлен один тихий
 * дом, видимый отовсюду: две строки третичного цвета в сайдбаре, между
 * навигацией и футером. Банда с экрана вопроса снята.</p>
 *
 * <p>Скоуп — только страничный {@link InterviewMvcController}: REST-контроллеры
 * модель не рендерят, и вешать на каждый API-вызов два COUNT-запроса было бы
 * платой ни за что. Логин-страница сайдбара не имеет.</p>
 *
 * <p>Цена — два лёгких запроса на рендер страницы: агрегированная статистика
 * банка ({@code getAggregatedStats} — один SQL вместо пяти счётчиков, см. её
 * javadoc) и COUNT вопросов с ошибками. Оба owner-scoped через
 * {@code OwnerContext} внутри репозитория.</p>
 */
@ControllerAdvice(assignableTypes = InterviewMvcController.class)
@ConditionalOnWebApplication
@RequiredArgsConstructor
public class SidebarStatusAdvice {

    private final InterviewFacade interviewFacade;
    private final QuestionStatsRepository questionStatsRepository;

    /**
     * Числа сайдбар-статуса.
     *
     * @param dueRepeats вопросы, которые уже открывали и пора повторить
     * @param wrongCount вопросы, где были неверные ответы (вход в разбор)
     */
    public record SidebarStatus(long dueRepeats, long wrongCount) {
        /** Есть ли что показывать: обе строки нулевые → блок не рендерится. */
        public boolean present() {
            return dueRepeats > 0 || wrongCount > 0;
        }
    }

    @ModelAttribute("sidebarStatus")
    public SidebarStatus sidebarStatus() {
        InterviewStats bank = interviewFacade.getStats(InterviewFilter.builder().build());
        long wrong = questionStatsRepository.countAllFiltered(null, null, true);
        return new SidebarStatus(bank.dueRepeats(), wrong);
    }
}
