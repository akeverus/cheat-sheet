package com.cheatsheet.quiz.common.util;

import org.springframework.stereotype.Component;

/**
 * Преобразование slug-а темы в человекочитаемое название для UI.
 */
@Component("topicUtils")
public class TopicUtils {

    /**
     * @param topic slug темы, например {@code "java/collections-interview.md"}
     * @return отображаемое имя, например {@code "collections"}
     */
    public String displayName(String topic) {
        if (topic == null || topic.isBlank()) {
            return "";
        }
        String name = topic;
        int lastSlash = name.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < name.length() - 1) {
            name = name.substring(lastSlash + 1);
        }
        name = name.replace("-interview", "").replace(".md", "");
        return name;
    }
}
