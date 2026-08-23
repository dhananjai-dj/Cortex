package com.project.util;

import org.springframework.ai.document.Document;

import java.util.List;

public class DocumentUtil {

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
}
