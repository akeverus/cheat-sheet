package com.cheatsheet.quiz.feature.interview.service.page;

import com.cheatsheet.quiz.common.util.TopicUtils;
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
    TopicUtils topicUtils;

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

    /**
     * Раздел каталога вместе со своими темами.
     *
     * <p>Числа шапки раздела считаются ПО СВОИМ СТРОКАМ, а не берутся из
     * {@link TopicCatalogService.GroupProgress}. Без отбора это одно и то же, а
     * при отборе — нет: замер на {@code ?q=spring} показывал «1 тема · 1224
     * вопроса», где тема отобранная, а вопросы всего раздела, и строка читалась
     * как «в этой теме 1224 вопроса». Шапка раскрытия обязана описывать то, что
     * внутри него лежит.
     */
    public record CatalogSection(TopicCatalogService.GroupProgress group, List<TopicRow> topics) {

        /** Вопросов в показанных темах раздела. */
        public long questionCount() {
            return topics.stream().mapToLong(TopicRow::total).sum();
        }

        /** Освоено в показанных темах раздела. */
        public long learnedCount() {
            return topics.stream().mapToLong(TopicRow::learned).sum();
        }

        /** Ждут повторения в показанных темах раздела. */
        public long repeatCount() {
            return topics.stream().mapToLong(TopicRow::repeats).sum();
        }
    }

    /**
     * Отбор каталога: строка поиска и состояние освоения.
     *
     * <p>Каталог из 319 тем нельзя отдавать одним списком: до этого отбора страница
     * занимала 18.8 экрана прокрутки на 1512×982 и не имела ни поиска, ни фильтра —
     * найти тему можно было только глазами по всей длине. Механизм взят тот же, что
     * на «Закладках»: GET-форма, значения в адресе, полоса отбора с чипами и
     * сбросом. Второго поиска в продукте не заводится.
     *
     * @param q     подстрока названия темы; пустая строка = без поиска
     * @param state {@code all} | {@code new} | {@code progress} | {@code due}
     */
    public record TopicsQuery(String q, String state) {

        /** Значения состояния, которые страница умеет отбирать. */
        public static final String ALL = "all";
        public static final String NEW = "new";
        public static final String PROGRESS = "progress";
        public static final String DUE = "due";

        /** Нормализует пришедшее из адреса: пустое и неизвестное = «все». */
        public static TopicsQuery of(String q, String state) {
            String trimmed = q == null ? "" : q.trim();
            String normalized = state == null ? ALL : state.trim().toLowerCase(java.util.Locale.ROOT);
            if (!NEW.equals(normalized) && !PROGRESS.equals(normalized) && !DUE.equals(normalized)) {
                normalized = ALL;
            }
            return new TopicsQuery(trimmed, normalized);
        }

        /** Отбор задан — значит полоса отбора показывает чипы и «Сбросить». */
        public boolean active() {
            return !q.isEmpty() || !ALL.equals(state);
        }

        /** Подходит ли строка каталога под текущий отбор. */
        boolean matches(TopicRow row, String displayName) {
            if (!q.isEmpty()) {
                String needle = q.toLowerCase(java.util.Locale.ROOT);
                boolean byName = displayName.toLowerCase(java.util.Locale.ROOT).contains(needle);
                boolean bySlug = row.topic().toLowerCase(java.util.Locale.ROOT).contains(needle);
                if (!byName && !bySlug) {
                    return false;
                }
            }
            return switch (state) {
                case NEW -> row.untouched();
                case DUE -> row.repeats() > 0;
                case PROGRESS -> !row.untouched() && row.learned() < row.total();
                default -> true;
            };
        }
    }

    /**
     * Состояние страницы.
     *
     * @param sections            разделы после отбора (пустые разделы не приходят)
     * @param topicCount          тем ВСЕГО в банке — число для шапки страницы
     * @param questionCount       вопросов всего в банке
     * @param matchedTopicCount   тем после отбора — число для полосы отбора
     * @param query               применённый отбор
     */
    public record TopicsPageState(List<CatalogSection> sections,
                                  long topicCount,
                                  long questionCount,
                                  long matchedTopicCount,
                                  TopicsQuery query) {}

    /** Собирает каталог из тех же источников, по которым потом идёт отбор сессии. */
    public TopicsPageState build(TopicsQuery query) {
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
        long matched = 0;
        for (TopicCatalogService.GroupProgress group : groups) {
            List<TopicRow> rows = new ArrayList<>();
            for (String topic : topicsByGroup.getOrDefault(group.id(), List.of())) {
                TopicStats s = statsByTopic.get(topic);
                long total = countsByTopic.getOrDefault(topic, 0L);
                TopicRow row = new TopicRow(
                        topic,
                        total,
                        s != null ? s.learned() : 0L,
                        s != null ? s.dueRepeats() : 0L,
                        s != null ? s.fresh() : total);
                // Счётчики шапки считаются по ВСЕМУ банку, а не по отобранному:
                // «319 тем · 9818 вопросов» — свойство каталога, а не запроса.
                topicCount++;
                questionCount += total;
                if (query.matches(row, topicUtils.displayName(topic))) {
                    rows.add(row);
                    matched++;
                }
            }
            if (!rows.isEmpty()) {
                result.add(new CatalogSection(group, rows));
            }
        }
        return new TopicsPageState(result, topicCount, questionCount, matched, query);
    }
}
