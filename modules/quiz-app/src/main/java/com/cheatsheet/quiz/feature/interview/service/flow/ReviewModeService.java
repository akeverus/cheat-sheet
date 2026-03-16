package com.cheatsheet.quiz.feature.interview.service.flow;

import com.cheatsheet.quiz.domain.InterviewFilter;
import org.springframework.stereotype.Service;

/**
 * Сервис правил review-режима.
 *
 * <p>В review режиме всегда принудительно включается фильтр onlyWrong=true,
 * чтобы показывать только вопросы с ошибками.</p>
 */
@Service
public class ReviewModeService {

    /**
     * Строит фильтр review-режима на основе обычного фильтра.
     *
     * @param baseFilter исходный фильтр
     * @return фильтр с включенным onlyWrong
     */
    public InterviewFilter apply(InterviewFilter baseFilter) {
        if (baseFilter == null) {
            return new InterviewFilter(null, null, null, true, null, true);
        }
        return new InterviewFilter(
                baseFilter.topic(),
                baseFilter.group(),
                baseFilter.importantOnly(),
                true,
                baseFilter.shuffle(),
                baseFilter.ordered()
        );
    }
}
