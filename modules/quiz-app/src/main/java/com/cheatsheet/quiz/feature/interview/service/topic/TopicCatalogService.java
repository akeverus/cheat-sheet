package com.cheatsheet.quiz.feature.interview.service.topic;

import com.cheatsheet.quiz.config.app.AppProperties;
import lombok.AccessLevel;
import com.cheatsheet.quiz.domain.InterviewFilter;
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
                options.add(new GroupOption(group.id(), group.title(), group.description(), count));
            }
        }
        return options;
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
                Map<String, Object> root = castRoot(rootMap);
                return parseTopicConfig(root);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load topic groups config: " + configPath, e);
        }
    }

    private static Map<String, Object> castRoot(Map<?, ?> raw) {
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
            groups.add(new GroupDef(id, title, description, order));
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
                result.add(castRoot(map));
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
        Set<String> keys = topics.stream().map(TopicDef::key).collect(java.util.stream.Collectors.toSet());
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

    @Builder(toBuilder = true)
    public record GroupOption(String id, String title, String description, long topicCount) {}

    @Builder(toBuilder = true)
    private record TopicConfig(List<GroupDef> groups, List<GroupRule> groupRules, List<TopicDef> topics) {}

    @Builder(toBuilder = true)
    private record GroupDef(String id, String title, String description, int order) {
        private GroupDef {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(title, "title");
            if (description == null) {
                description = "";
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
