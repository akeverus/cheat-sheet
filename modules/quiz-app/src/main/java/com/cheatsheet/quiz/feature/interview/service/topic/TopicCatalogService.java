package com.cheatsheet.quiz.feature.interview.service.topic;

import com.cheatsheet.quiz.config.app.AppProperties;
import lombok.AccessLevel;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.TopicStats;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import lombok.Builder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Каталог тем: группы, порядок и учебные траектории из YAML-конфига.
 */
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicCatalogService {

    static final int FALLBACK_TOPIC_ORDER = 10_000;
    static final int FALLBACK_GROUP_ORDER = 10_000;
    static final String DEFAULT_CONFIG_PATH = "topic-groups.yaml";

    Map<String, GroupDef> groupsById;
    List<GroupDef> groupsOrdered;
    List<GroupRule> groupRules;
    Map<String, TopicDef> explicitTopicsByKey;
    String defaultGroupId;

    public TopicCatalogService(AppProperties properties) {
        String configPath = properties.getTopicGroupsConfig();
        if (configPath == null || configPath.isBlank()) {
            configPath = DEFAULT_CONFIG_PATH;
        }
        TopicConfig config = loadConfig(configPath);
        validateConfig(config);
        this.groupsById = indexGroups(config.groups);
        this.groupsOrdered = this.groupsById.values().stream()
                .sorted(Comparator.comparingInt(GroupDef::order).thenComparing(GroupDef::id))
                .toList();
        this.groupRules = List.copyOf(config.groupRules);
        this.explicitTopicsByKey = indexTopics(config.topics);
        this.defaultGroupId = this.groupsById.containsKey("misc") ? "misc" : null;
        log.info("Topic catalog loaded: groups={}, rules={}, explicitTopics={}",
                groupsById.size(), groupRules.size(), explicitTopicsByKey.size());
    }

    public String normalizeGroup(String group) {
        if (group == null || group.isBlank()) {
            return null;
        }
        String normalized = group.strip();
        return groupsById.containsKey(normalized) ? normalized : null;
    }

    public Optional<String> detectGroupForTopic(String topicKey) {
        return Optional.ofNullable(detectGroup(topicKey));
    }

    public List<String> topicsForFilter(InterviewFilter filter, List<String> availableTopics) {
        if (filter == null) {
            return filterAndSortTopics(availableTopics, null, true);
        }
        if (filter.topic() != null && !filter.topic().isBlank()) {
            return List.of(filter.topic().strip());
        }
        String group = normalizeGroup(filter.group());
        return filterAndSortTopics(availableTopics, group, filter.isOrdered());
    }

    public List<String> filterAndSortTopics(List<String> availableTopics, String group, boolean ordered) {
        if (availableTopics == null || availableTopics.isEmpty()) {
            return List.of();
        }
        String normalizedGroup = normalizeGroup(group);
        Set<String> unique = new LinkedHashSet<>();
        for (String topic : availableTopics) {
            if (topic == null || topic.isBlank()) {
                continue;
            }
            String normalizedTopic = topic.strip();
            if (normalizedGroup != null) {
                String topicGroup = detectGroup(normalizedTopic);
                if (!normalizedGroup.equals(topicGroup)) {
                    continue;
                }
            }
            unique.add(normalizedTopic);
        }
        List<String> result = new ArrayList<>(unique);
        if (ordered) {
            result.sort(this::compareTopicsByCatalogOrder);
        } else {
            result.sort(String::compareTo);
        }
        return result;
    }

    public List<GroupOption> groupOptions(List<String> availableTopics) {
        Map<String, Long> counters = new HashMap<>();
        for (GroupDef group : groupsOrdered) {
            counters.put(group.id(), 0L);
        }
        if (availableTopics != null) {
            for (String topic : availableTopics) {
                String groupId = detectGroup(topic);
                if (groupId != null) {
                    counters.put(groupId, counters.getOrDefault(groupId, 0L) + 1L);
                }
            }
        }
        List<GroupOption> options = new ArrayList<>();
        for (GroupDef group : groupsOrdered) {
            long count = counters.getOrDefault(group.id(), 0L);
            if (count > 0) {
                options.add(new GroupOption(group.id(), group.title(), group.description(), count, group.icon()));
            }
        }
        return options;
    }

    /**
     * Собирает карточки разделов для каталога тем: охват — из банка вопросов,
     * прогресс — из потемной статистики.
     *
     * <p>Каталог из ~320 тем нельзя обозреть: таблица «Детализация по темам»
     * отвечает на вопрос «как дела с темой X», но не на вопрос «с чего начать».
     * Раздел — единственный уровень, на котором список умещается в один экран,
     * поэтому агрегация идёт именно до него: десять карточек вместо трёхсот
     * строк.
     *
     * <p>Два источника, а не один: состав раздела задаёт банк вопросов
     * ({@code questionCountsByTopic}), прогресс накладывается сверху и по
     * умолчанию равен нулю. Тема, попавшая только в статистику, в каталог не
     * добавляется — иначе раздел показал бы «изучено больше, чем есть».
     *
     * <p>Разделы без единого вопроса не рендерятся: карточка «0 из 0» сообщала
     * бы о конфиге, а не о прогрессе.
     *
     * <p>Точность — {@code null}, когда по разделу не было ни одного ответа.
     * Ноль процентов означал бы «отвечал и всё неверно»; нетронутый раздел
     * обязан выглядеть иначе, чем полностью проваленный.
     */
    public List<GroupProgress> groupCatalog(Map<String, Long> questionCountsByTopic, List<TopicStats> topicStats) {
        if (questionCountsByTopic == null || questionCountsByTopic.isEmpty()) {
            return List.of();
        }
        // [темы, вопросы, изучено, готовы к показу, верно, неверно, новые]
        Map<String, long[]> sums = new HashMap<>();
        for (Map.Entry<String, Long> entry : questionCountsByTopic.entrySet()) {
            String groupId = detectGroup(entry.getKey());
            if (groupId == null) {
                continue;
            }
            long[] acc = sums.computeIfAbsent(groupId, key -> new long[7]);
            acc[0] += 1;
            acc[1] += entry.getValue() == null ? 0L : entry.getValue();
        }
        if (topicStats != null) {
            for (TopicStats stats : topicStats) {
                if (stats == null) {
                    continue;
                }
                String groupId = detectGroup(stats.topic());
                // Тема с прогрессом, но без вопросов в банке — вопросы удалили,
                // след в review_state остался. Её нет в каталоге, и прогресс по
                // ней некуда класть: раздел мог бы показать «изучено больше, чем
                // есть», и полоса ушла бы за 100%.
                if (groupId == null || !sums.containsKey(groupId)) {
                    continue;
                }
                long[] acc = sums.get(groupId);
                acc[2] += stats.learned();
                acc[3] += stats.due();
                acc[4] += stats.correct();
                acc[5] += stats.wrong();
                acc[6] += stats.fresh();
            }
        }
        List<GroupProgress> catalog = new ArrayList<>();
        for (GroupDef group : groupsOrdered) {
            long[] acc = sums.get(group.id());
            if (acc == null || acc[1] == 0) {
                continue;
            }
            long attempts = acc[4] + acc[5];
            Double accuracy = attempts > 0 ? acc[4] * 100.0 / attempts : null;
            catalog.add(new GroupProgress(group.id(), group.title(), group.description(), group.icon(),
                    acc[0], acc[1], Math.min(acc[2], acc[1]), acc[3], Math.min(acc[6], acc[3]), accuracy));
        }
        return catalog;
    }

    /**
     * Разбивает уже отсортированный список тем на секции групп — ровно тот
     * порядок, в котором темы пришли, только с видимыми границами.
     *
     * <p>Селект тем несёт ~320 позиций. Каталог и так сортирует их по группам
     * ({@link #compareTopicsByCatalogOrder}), но в плоском {@code <select>} эта
     * структура невидима: «Группа» отдельным контролем сверху лишь фильтрует и
     * перезагружает страницу. Секции делают существующую группировку читаемой,
     * ничего не меняя в отборе.
     *
     * <p>Порядок внутри секции — входной, не пересортировывается: список уже
     * отражает выбор пользователя «Учебный / Свободный» ({@code ordered}), и
     * повторная сортировка молча отменяла бы его.
     */
    public List<TopicSection> groupTopics(List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        Map<String, List<String>> byGroup = new LinkedHashMap<>();
        for (String topic : topics) {
            if (topic == null || topic.isBlank()) {
                continue;
            }
            String normalized = topic.strip();
            String groupId = detectGroup(normalized);
            byGroup.computeIfAbsent(groupId == null ? "" : groupId, key -> new ArrayList<>()).add(normalized);
        }
        List<TopicSection> sections = new ArrayList<>();
        for (GroupDef group : groupsOrdered) {
            List<String> groupTopics = byGroup.remove(group.id());
            if (groupTopics != null && !groupTopics.isEmpty()) {
                sections.add(new TopicSection(group.id(), group.title(), List.copyOf(groupTopics)));
            }
        }
        // Темы без группы (нет правила и нет misc в конфиге) не должны исчезать
        // из селекта: секция без заголовка теряет смысл, поэтому им даётся явная.
        List<String> ungrouped = byGroup.values().stream().flatMap(List::stream).toList();
        if (!ungrouped.isEmpty()) {
            sections.add(new TopicSection("", "Без группы", ungrouped));
        }
        return sections;
    }

    private int compareTopicsByCatalogOrder(String left, String right) {
        int leftGroupOrder = groupOrder(left);
        int rightGroupOrder = groupOrder(right);
        if (leftGroupOrder != rightGroupOrder) {
            return Integer.compare(leftGroupOrder, rightGroupOrder);
        }
        int leftTopicOrder = topicOrder(left);
        int rightTopicOrder = topicOrder(right);
        if (leftTopicOrder != rightTopicOrder) {
            return Integer.compare(leftTopicOrder, rightTopicOrder);
        }
        return left.compareTo(right);
    }

    private int topicOrder(String topicKey) {
        TopicDef explicit = explicitTopicsByKey.get(topicKey);
        return explicit != null ? explicit.order() : FALLBACK_TOPIC_ORDER;
    }

    private int groupOrder(String topicKey) {
        String groupId = detectGroup(topicKey);
        if (groupId == null) {
            return FALLBACK_GROUP_ORDER;
        }
        GroupDef group = groupsById.get(groupId);
        return group != null ? group.order() : FALLBACK_GROUP_ORDER;
    }

    private String detectGroup(String topicKey) {
        if (topicKey == null || topicKey.isBlank()) {
            return null;
        }
        String normalized = topicKey.strip();
        TopicDef explicit = explicitTopicsByKey.get(normalized);
        if (explicit != null) {
            return explicit.groupId();
        }
        for (GroupRule rule : groupRules) {
            for (String prefix : rule.prefixes()) {
                if (normalized.startsWith(prefix)) {
                    return rule.groupId();
                }
            }
        }
        return defaultGroupId;
    }

    private static Map<String, GroupDef> indexGroups(List<GroupDef> groups) {
        Map<String, GroupDef> result = new LinkedHashMap<>();
        for (GroupDef group : groups) {
            result.put(group.id(), group);
        }
        return result;
    }

    private static Map<String, TopicDef> indexTopics(List<TopicDef> topics) {
        Map<String, TopicDef> result = new LinkedHashMap<>();
        for (TopicDef topic : topics) {
            result.put(topic.key(), topic);
        }
        return result;
    }

    private TopicConfig loadConfig(String configPath) {
        try {
            ClassPathResource resource = new ClassPathResource(configPath);
            if (!resource.exists()) {
                throw new IllegalStateException("Topic groups config not found: " + configPath);
            }
            try (InputStream in = resource.getInputStream()) {
                Object loaded = new Yaml().load(in);
                if (!(loaded instanceof Map<?, ?> rootMap)) {
                    throw new IllegalStateException("Topic groups config has invalid root structure");
                }
                Map<String, Object> root = toStringKeyedMap(rootMap);
                return parseTopicConfig(root);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load topic groups config: " + configPath, e);
        }
    }

    private static Map<String, Object> toStringKeyedMap(Map<?, ?> raw) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            if (entry.getKey() != null) {
                result.put(entry.getKey().toString(), entry.getValue());
            }
        }
        return result;
    }

    private static TopicConfig parseTopicConfig(Map<String, Object> root) {
        List<GroupDef> groups = new ArrayList<>();
        for (Map<String, Object> item : asMapList(root.get("groups"))) {
            String id = asRequiredString(item, "id");
            String title = asRequiredString(item, "title");
            String description = asOptionalString(item, "description");
            int order = asInt(item.get("order"), FALLBACK_GROUP_ORDER);
            String icon = asOptionalString(item, "icon");
            groups.add(new GroupDef(id, title, description, order, icon));
        }

        List<GroupRule> rules = new ArrayList<>();
        for (Map<String, Object> item : asMapList(root.get("groupRules"))) {
            String groupId = asRequiredString(item, "groupId");
            List<String> prefixes = asStringList(item.get("prefixes"));
            rules.add(new GroupRule(groupId, prefixes));
        }

        List<TopicDef> topics = new ArrayList<>();
        for (Map<String, Object> item : asMapList(root.get("topics"))) {
            String key = asRequiredString(item, "key");
            String groupId = asRequiredString(item, "groupId");
            int order = asInt(item.get("order"), FALLBACK_TOPIC_ORDER);
            List<String> prerequisites = asStringList(item.get("prerequisites"));
            topics.add(new TopicDef(key, groupId, order, prerequisites));
        }
        return new TopicConfig(groups, rules, topics);
    }

    private static List<Map<String, Object>> asMapList(Object value) {
        if (!(value instanceof Collection<?> collection)) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : collection) {
            if (item instanceof Map<?, ?> map) {
                result.add(toStringKeyedMap(map));
            }
        }
        return result;
    }

    private static List<String> asStringList(Object value) {
        if (!(value instanceof Collection<?> collection)) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (Object item : collection) {
            if (item == null) {
                continue;
            }
            String str = item.toString().strip();
            if (!str.isBlank()) {
                result.add(str);
            }
        }
        return result;
    }

    private static String asRequiredString(Map<String, Object> map, String key) {
        String value = asOptionalString(map, key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required field: " + key);
        }
        return value;
    }

    private static String asOptionalString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String str = value.toString().strip();
        return str.isBlank() ? null : str;
    }

    private static int asInt(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString().strip());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private void validateConfig(TopicConfig config) {
        if (config.groups.isEmpty()) {
            throw new IllegalStateException("topic-groups.yaml: groups must not be empty");
        }
        ensureUniqueGroupIds(config.groups);
        ensureUniqueTopicKeys(config.topics);
        ensureGroupReferences(config);
        ensurePrerequisites(config.topics);
        ensureNoPrerequisiteCycles(config.topics);
    }

    private static void ensureUniqueGroupIds(List<GroupDef> groups) {
        Set<String> ids = new HashSet<>();
        for (GroupDef group : groups) {
            if (!ids.add(group.id())) {
                throw new IllegalStateException("Duplicate group id in topic-groups.yaml: " + group.id());
            }
        }
    }

    private static void ensureUniqueTopicKeys(List<TopicDef> topics) {
        Set<String> keys = new HashSet<>();
        for (TopicDef topic : topics) {
            if (!keys.add(topic.key())) {
                throw new IllegalStateException("Duplicate topic key in topic-groups.yaml: " + topic.key());
            }
        }
    }

    private static void ensureGroupReferences(TopicConfig config) {
        Set<String> groupIds = new HashSet<>();
        for (GroupDef group : config.groups) {
            groupIds.add(group.id());
        }
        for (TopicDef topic : config.topics) {
            if (!groupIds.contains(topic.groupId())) {
                throw new IllegalStateException("Unknown groupId for topic " + topic.key() + ": " + topic.groupId());
            }
        }
        for (GroupRule rule : config.groupRules) {
            if (!groupIds.contains(rule.groupId())) {
                throw new IllegalStateException("Unknown groupId in groupRules: " + rule.groupId());
            }
            if (rule.prefixes().isEmpty()) {
                throw new IllegalStateException("Empty prefixes for groupRules groupId=" + rule.groupId());
            }
        }
    }

    private static void ensurePrerequisites(List<TopicDef> topics) {
        Set<String> keys = topics.stream().map(TopicDef::key).collect(Collectors.toSet());
        for (TopicDef topic : topics) {
            for (String prerequisite : topic.prerequisites()) {
                if (!keys.contains(prerequisite)) {
                    throw new IllegalStateException("Unknown prerequisite for topic " + topic.key() + ": " + prerequisite);
                }
            }
        }
    }

    private static void ensureNoPrerequisiteCycles(List<TopicDef> topics) {
        Map<String, TopicDef> byKey = new HashMap<>();
        for (TopicDef topic : topics) {
            byKey.put(topic.key(), topic);
        }
        Set<String> visited = new HashSet<>();
        Set<String> active = new HashSet<>();
        for (TopicDef topic : topics) {
            visit(topic.key(), byKey, visited, active);
        }
    }

    private static void visit(String key, Map<String, TopicDef> byKey, Set<String> visited, Set<String> active) {
        if (visited.contains(key)) {
            return;
        }
        if (!active.add(key)) {
            throw new IllegalStateException("Cycle detected in topic prerequisites at: " + key);
        }
        TopicDef topic = byKey.get(key);
        if (topic != null) {
            for (String prerequisite : topic.prerequisites()) {
                visit(prerequisite, byKey, visited, active);
            }
        }
        active.remove(key);
        visited.add(key);
    }

    /**
     * @param icon путь к иконке раздела относительно {@code /img/} (может быть
     *             пустым) — см. {@code icon} в {@code topic-groups.yaml}
     */
    @Builder(toBuilder = true)
    public record GroupOption(String id, String title, String description, long topicCount, String icon) {}

    /** Секция селекта тем: заголовок группы и её темы во входном порядке. */
    @Builder(toBuilder = true)
    public record TopicSection(String id, String title, List<String> topics) {}

    /**
     * Карточка раздела в каталоге тем: чем раздел является и как по нему дела.
     *
     * @param icon            путь относительно {@code /img/}, может быть пустым
     * @param topicCount      сколько тем раздела есть в банке вопросов
     * @param questionCount   сколько всего вопросов в этих темах
     * @param learnedCount    сколько вопросов доведено до «изучено»
     * @param dueCount        сколько вопросов готовы к показу: и повторения, и новые
     * @param freshCount      сколько вопросов ещё ни разу не открывали
     * @param accuracyPercent доля верных ответов или {@code null}, если ответов
     *                        по разделу ещё не было
     */
    @Builder(toBuilder = true)
    public record GroupProgress(
            String id,
            String title,
            String description,
            String icon,
            long topicCount,
            long questionCount,
            long learnedCount,
            long dueCount,
            long freshCount,
            Double accuracyPercent
    ) {
        /** Доля изученного в процентах — ширина полосы прогресса на карточке. */
        public double learnedPercent() {
            return questionCount > 0 ? learnedCount * 100.0 / questionCount : 0.0;
        }

        /**
         * Вопросы, которые уже открывали и пора открыть снова. Отделены от
         * {@link #freshCount()}, потому что «повторить» и «пройти впервые» — разные
         * обещания, и раздел без единого ответа не может звать повторять.
         */
        public long repeatCount() {
            return Math.max(0L, dueCount - freshCount);
        }
    }

    @Builder(toBuilder = true)
    private record TopicConfig(List<GroupDef> groups, List<GroupRule> groupRules, List<TopicDef> topics) {}

    /**
     * @param icon путь к пиксель-иконке раздела относительно {@code /img/} —
     *             опознавательный знак группы в каталоге тем. Живёт в конфиге
     *             рядом с заголовком, потому что это часть определения раздела:
     *             при добавлении группы иконка задаётся там же, где всё
     *             остальное, а не в отдельной таблице соответствий в шаблоне.
     *             Пустой — карточка каталога рендерится без картинки.
     */
    @Builder(toBuilder = true)
    private record GroupDef(String id, String title, String description, int order, String icon) {
        private GroupDef {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(title, "title");
            if (description == null) {
                description = "";
            }
            if (icon == null) {
                icon = "";
            }
        }
    }

    @Builder(toBuilder = true)
    private record GroupRule(String groupId, List<String> prefixes) {
        private GroupRule {
            Objects.requireNonNull(groupId, "groupId");
            prefixes = prefixes == null ? List.of() : List.copyOf(prefixes);
        }
    }

    @Builder(toBuilder = true)
    private record TopicDef(String key, String groupId, int order, List<String> prerequisites) {
        private TopicDef {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(groupId, "groupId");
            prerequisites = prerequisites == null ? List.of() : List.copyOf(prerequisites);
        }
    }
}
