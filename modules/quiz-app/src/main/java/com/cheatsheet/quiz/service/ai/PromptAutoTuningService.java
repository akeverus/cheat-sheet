package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Держит динамический quality-контекст, который добавляется в prompt между итерациями.
 */
@Component
public class PromptAutoTuningService {
    private final Map<OptionQualityValidator.IssueCode, String> ruleByIssue = buildRuleMap();
    private final int maxPromptRules;
    private List<OptionQualityValidator.IssueCode> activeRules = new ArrayList<>();

    public PromptAutoTuningService(AppProperties appProperties) {
        this.maxPromptRules = appProperties.getQualityIterations().getMaxPromptRules();
    }

    public synchronized void reset() {
        activeRules = new ArrayList<>();
    }

    public synchronized String currentQualityContext() {
        if (activeRules.isEmpty()) {
            return "";
        }
        StringBuilder context = new StringBuilder("AUTO_TUNED_RULES:\n");
        int index = 1;
        for (OptionQualityValidator.IssueCode issueCode : activeRules) {
            String rule = ruleByIssue.get(issueCode);
            if (rule == null || rule.isBlank()) {
                continue;
            }
            context.append(index++).append(". ").append(rule).append('\n');
        }
        return context.toString().trim();
    }

    public synchronized String applyTopIssues(Map<OptionQualityValidator.IssueCode, Integer> issueCounts) {
        if (issueCounts == null || issueCounts.isEmpty()) {
            activeRules = new ArrayList<>();
            return "";
        }
        List<OptionQualityValidator.IssueCode> selected = issueCounts.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() > 0)
                .sorted(Comparator
                        .<Map.Entry<OptionQualityValidator.IssueCode, Integer>>comparingInt(Map.Entry::getValue)
                        .reversed()
                        .thenComparing(entry -> entry.getKey().name()))
                .map(Map.Entry::getKey)
                .filter(ruleByIssue::containsKey)
                .limit(maxPromptRules)
                .toList();
        activeRules = new ArrayList<>(selected);
        return currentQualityContext();
    }

    private static Map<OptionQualityValidator.IssueCode, String> buildRuleMap() {
        Map<OptionQualityValidator.IssueCode, String> rules = new EnumMap<>(OptionQualityValidator.IssueCode.class);
        rules.put(OptionQualityValidator.IssueCode.VAGUE_OPTION_TEXT,
                "Каждый вариант обязан содержать конкретный проверяемый факт: API-контракт, условие, сложность, исключение или явный результат.");
        rules.put(OptionQualityValidator.IssueCode.DUPLICATE_OPTIONS,
                "Строго запрети любые семантические и лексические дубли: каждый вариант обязан отличаться по сути, а не перестановкой слов.");
        rules.put(OptionQualityValidator.IssueCode.WRONG_TOO_CLOSE_TO_CORRECT,
                "Wrong-варианты должны быть правдоподобными, но с явной фактической ошибкой, чтобы не создавать двусмысленность.");
        rules.put(OptionQualityValidator.IssueCode.AMBIGUOUS_CORRECT_ANSWER,
                "Correct запрещено формулировать с хеджированием (обычно/иногда/может) без явно указанного условия.");
        rules.put(OptionQualityValidator.IssueCode.PATH_LIKE_OPTION_TEXT,
                "Полный запрет на path/slug/URL, имена файлов, исходные пути репозитория и служебные артефакты в option text.");
        rules.put(OptionQualityValidator.IssueCode.CORRECT_ANSWER_MISMATCH,
                "Correct должен прямо соответствовать эталонному ответу и включать его ключевую сущность без подмены тезиса.");
        rules.put(OptionQualityValidator.IssueCode.ANSWER_TYPE_MISMATCH,
                "Тип ответа обязан совпадать с типом вопроса: для code-result — конкретный результат/исключение, для compare — критерий сравнения.");
        rules.put(OptionQualityValidator.IssueCode.MISSING_COMPARISON_CRITERIA,
                "Для вопросов-сравнений каждый вариант должен содержать явный критерий: сложность, память, latency, гарантии или условия применимости.");
        rules.put(OptionQualityValidator.IssueCode.TRUNCATED_OPTION_TEXT,
                "Запрещены обрезанные варианты и многоточия; каждый вариант должен быть завершённой фразой.");
        return rules;
    }
}
