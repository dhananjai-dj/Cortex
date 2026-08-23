package com.project.dto;

import java.util.Map;

public record InjectRequest(String summary, Map<String, Object> metaData) {
}
