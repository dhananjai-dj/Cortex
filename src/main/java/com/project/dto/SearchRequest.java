package com.project.dto;

public record SearchRequest(String query, Double minScore, Integer limit) {
}
