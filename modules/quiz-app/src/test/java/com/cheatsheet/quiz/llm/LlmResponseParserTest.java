package com.cheatsheet.quiz.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmResponseParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void extractsContentFromOpenAiCompatibleResponse() throws Exception {
        String json = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "```json\\n{\\\"correct\\\":\\\"A\\\",\\\"wrong\\\":[\\\"B\\\",\\\"C\\\"]}\\n```"
                      }
                    }
                  ]
                }
                """;

        String content = LlmResponseParser.extractContent(json, objectMapper);
        assertThat(content).contains("\"correct\":\"A\"");
    }
}
