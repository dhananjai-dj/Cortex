package com.project.dto;

public record KbSearchRequest(String query, Double minScore, Integer limit) {
}
