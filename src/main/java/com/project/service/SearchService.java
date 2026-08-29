package com.project.service;

import com.project.dto.KbResult;
import com.project.dto.KbSearchRequest;
import com.project.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SearchService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final VectorStore vectorStore;
    private final SummaryService summaryService;

    public SearchService(VectorStore vectorStore, SummaryService summaryService) {
        this.vectorStore = vectorStore;
        this.summaryService = summaryService;
    }

    public List<KbResult> searchData(KbSearchRequest kbSearchRequest) {
        List<KbResult> result = null;
        try {
            SearchRequest searchRequest = generateSearchRequest(kbSearchRequest);
            List<Document> documentList = vectorStore.similaritySearch(searchRequest);
            if (!DocumentUtil.isNonEmptyDocumentList(documentList)) {
                logger.warn("Document list is empty");
                return List.of(KbResult.defaultResult());
            }
            result = new ArrayList<>(documentList.size());
            for (Document document : documentList) {
                KbResult kbResult = KbResult.parseDocument(document);
                if (kbResult != null) {
                    result.add(kbResult);
                }
            }
            result.sort(Comparator.comparing(SearchService::parseTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));
            List<KbResult> summaryResult = summaryService.generateResult(result);
            if (!summaryResult.isEmpty()) {
                return summaryResult;
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            logger.error("Error in searching query {}", e.getMessage());
        }
        return List.of(KbResult.defaultResult());
    }

    private static LocalDateTime parseTimestamp(KbResult kbResult) {
        try {
            return LocalDateTime.parse(kbResult.timestamp(), TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private SearchRequest generateSearchRequest(KbSearchRequest kbSearchRequest) {
        try {
            String query = kbSearchRequest.query();
            int limit = kbSearchRequest.limit() != null ? Math.max(5, kbSearchRequest.limit()) : 5;
            double minScore = kbSearchRequest.minScore() != null ? Math.min(DocumentUtil.getSimilarBoundaryScore(), kbSearchRequest.minScore()) : DocumentUtil.getSimilarBoundaryScore();
            return SearchRequest.builder().query(query).similarityThreshold(minScore).topK(limit).build();
        } catch (Exception e) {
            logger.error("Error in generating searching Request Object {}", e.getMessage());
        }
        return SearchRequest.builder().query(kbSearchRequest.query()).build();
    }

}
