package com.project.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;

import java.sql.Timestamp;

public record KbResult(String content, String author, String microservice, String timestamp) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public @NonNull String toString() {
        return "KbResult{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", microservice='" + microservice + '\'' +
                ", date='" + timestamp + '\'' +
                '}';
    }

    public static KbResult defaultResult() {
        return new KbResult("Content Not found. I don't have any Knowledge about this!!!", null, null, null);
    }

    public static KbResult parse(String string) {
        try {
            return OBJECT_MAPPER.readValue(string, KbResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON string into KbResult", e);
        }
    }
}