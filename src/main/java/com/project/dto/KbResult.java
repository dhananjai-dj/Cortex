package com.project.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public record KbResult(String content, String author, String microservice, String timestamp, String classification) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(KbResult.class);

    @Override
    public @NonNull String toString() {
        return "KbResult{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", microservice='" + microservice + '\'' +
                ", date='" + timestamp + '\'' +
                ", classification=" + classification +
                '}';
    }

    public static KbResult defaultResult() {
        return new KbResult("Content Not found. I don't have any Knowledge about this!!!", null, null, null, null);
    }

    public static KbResult parse(String string) {
        try {
            return OBJECT_MAPPER.readValue(string, KbResult.class);
        } catch (Exception e) {
            log.error("KbResult parse error of the string {} ", string, e);
            throw new RuntimeException(e);
        }
    }
}