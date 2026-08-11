package com.cheatsheet.quiz.feature.interview.service.page;

import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Страница «Темы» — каталог банка вопросов.
 *
 * <p>До неё пункт «Темы» первого уровня вёл на {@code /stats#topics}, то есть на
 * якорь ВНУТРИ аналитики. Признак подделки был записан в самой разметке: у этого
 * пункта, единственного из шести, не было ни {@code active}-состояния, ни
 * {@code aria-current} — подсвечивать нечего, когда назначение не является
 * страницей. В мобильной навигации его не было вовсе, и первый уровень
 * расходился между desktop и mobile.
 *
 * <p>Каталог отвечает на другой вопрос, чем аналитика. Аналитика — «как я иду»;
 * каталог — «что вообще есть и куда пойти». Поэтому здесь нет ни графиков, ни
 * точности по попыткам: раздел, тема, объём, освоено, ждёт повторения.
 *
 * <p>Своей агрегации сервис не заводит: разделы, темы и статистика уже считаются
 * теми же методами для аналитики и логина. Вторая копия расчёта разошлась бы с
 * первой, и каталог показывал бы не тот банк, по которому потом идёт сессия.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicsPageService {

    InterviewFacade facade;
    TopicCatalogService topicCatalogService;
    QuestionRepository questionRepository;

    /**
     * Строка каталога: тема и её состояние.
     *
     * @param topic   имя темы (оно же ключ отбора сессии)
     * @param total   всего вопросов в теме
     * @param learned освоено
     * @param repeats ждут повторения (уже отвечали, срок наступил)
     * @param fresh   ни разу не открывали
     */
    public record TopicRow(String topic, long total, long learned, long repeats, long fresh) {

        /** Доля освоенного — она же подпись состояния. */
        public double learnedPercent() {
            return total > 0 ? learned * 100.0 / total : 0.0;
        }

        /** Тема, которую ещё не начинали: обещать «повторить» на ней нельзя. */
        public boolean untouched() {
            return learned == 0 && repeats == 0;
        }
    }

    /** Раздел каталога вместе со своими темами. */
    public record CatalogSection(TopicCatalogService.GroupProgress group, List<TopicRow> topics) {}

    /** Состояние страницы: только каталог. */
    public record TopicsPageState(List<CatalogSection> sections, long topicCount, long questionCount) {}

    /** Собирает каталог из тех же источников, по которым потом идёт отбор сессии. */
    public TopicsPageState build() {
        Map<String, Long> countsByTopic = questionRepository.countQuestionsByTopic();
        List<TopicStats> stats = facade.getTopicStats();
        Map<String, TopicStats> statsByTopic = new LinkedHashMap<>();
        for (TopicStats s : stats) {
            statsByTopic.put(s.topic(), s);
        }

        List<TopicCatalogService.GroupProgress> groups =
                topicCatalogService.groupCatalog(countsByTopic, stats);
        List<TopicCatalogService.TopicSection> sections =
                topicCatalogService.groupTopics(new ArrayList<>(countsByTopic.keySet()));

        Map<String, List<String>> topicsByGroup = new LinkedHashMap<>();
        for (TopicCatalogService.TopicSection section : sections) {
            topicsByGroup.put(section.id(), section.topics());
        }

        List<CatalogSection> result = new ArrayList<>();
        long topicCount = 0;
        long questionCount = 0;
        for (TopicCatalogService.GroupProgress group : groups) {
            List<TopicRow> rows = new ArrayList<>();
            for (String topic : topicsByGroup.getOrDefault(group.id(), List.of())) {
                TopicStats s = statsByTopic.get(topic);
                long total = countsByTopic.getOrDefault(topic, 0L);
                rows.add(new TopicRow(
                        topic,
                        total,
                        s != null ? s.learned() : 0L,
                        s != null ? s.dueRepeats() : 0L,
                        s != null ? s.fresh() : total));
                topicCount++;
                questionCount += total;
            }
            if (!rows.isEmpty()) {
                result.add(new CatalogSection(group, rows));
            }
        }
        return new TopicsPageState(result, topicCount, questionCount);
    }
}
