package com.project.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;

public record KbResult(String content, String author, String microservice) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public @NonNull String toString() {
        return "KbResult{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", microservice='" + microservice + '\'' +
                '}';
    }

    public static KbResult parse(String string) {
        try {
            return OBJECT_MAPPER.readValue(string, KbResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON string into KbResult", e);
        }
    }
}