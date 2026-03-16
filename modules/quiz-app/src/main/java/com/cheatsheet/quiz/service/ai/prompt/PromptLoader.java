package com.cheatsheet.quiz.service.ai.prompt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import lombok.experimental.UtilityClass;

/**
 * Loads prompt templates from classpath resources under {@code prompts/}.
 * Caches loaded content in memory for reuse.
 */
@UtilityClass
public class PromptLoader {

    private static final String PROMPTS_PREFIX = "prompts/";
    private static final String SUFFIX = ".txt";

    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();

    /**
     * Loads a prompt template from classpath resource {@code prompts/{name}.txt}.
     *
     * @param name base name of the prompt file (without .txt)
     * @return the file content as string (UTF-8)
     * @throws IllegalArgumentException if the resource is not found or cannot be read
     */
    public static String load(String name) {
        return CACHE.computeIfAbsent(name, PromptLoader::readFromClasspath);
    }

    private static String readFromClasspath(String name) {
        String path = PROMPTS_PREFIX + name + SUFFIX;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = PromptLoader.class.getClassLoader();
        }
        try (InputStream is = cl.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("Prompt resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read prompt: " + path, e);
        }
    }
}
