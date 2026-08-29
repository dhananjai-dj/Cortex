package com.project.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class DocumentUtil {

    private static final Logger logger = LoggerFactory.getLogger(DocumentUtil.class);

    private static final double SIMILAR_BOUNDARY_SCORE = 0.85;

    public static double getSimilarBoundaryScore() {
        return SIMILAR_BOUNDARY_SCORE;
    }

    public static boolean isSimilarDocument(Document document) {
        return getDocumentScore(document) >= SIMILAR_BOUNDARY_SCORE;
    }

    public static double getDocumentScore(Document document) {
        return (document != null && document.getScore() != null) ? document.getScore() : 0;
    }

    public static boolean isNonEmptyDocumentList(List<Document> documentList) {
        return !documentList.isEmpty();
    }

    public static String extractMetaDataValue(Document document, String key) {
        try {
            if (document != null) {
                Map<String, Object> metaData = document.getMetadata();
                Object result = metaData.get(key);
                return result != null ? result.toString() : "";
            }
        } catch (Exception e) {
            logger.error("Error in extracting meta data from the document {}", e.getMessage());
        }
        return "";
    }

    public static String toString(Document document) {
        if (document != null) {
            return document.getText() + "\n Meta Data Details:\n" + getMetaDataAsString(document);
        }
        return "";
    }

    public static String getMetaDataAsString(Document document) {
        if (document != null) {
            Map<String, Object> metaData = document.getMetadata();
            StringBuilder metaDataString = new StringBuilder();
            if (!metaData.isEmpty()) {
                for (Map.Entry<String, Object> entry : metaData.entrySet()) {
                    metaDataString.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
                }
            }
            return metaDataString.toString();
        }
        return "";
    }

    public static String encodeString(String string) {
        if (string == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(string.getBytes());
    }
}
