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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final VectorStore vectorStore;
    private final SummaryService summaryService;

    public SearchService(VectorStore vectorStore, SummaryService summaryService) {
        this.vectorStore = vectorStore;
        this.summaryService = summaryService;
    }

    public List<KbResult> searchData(KbSearchRequest kbSearchRequest) {
        List<KbResult> result = new ArrayList<>();
        try {
            SearchRequest searchRequest = getSearchRequest(kbSearchRequest);
            List<Document> documentList = vectorStore.similaritySearch(searchRequest);
            if (DocumentUtil.isNonEmptyDocumentList(documentList)) {
                for (Document document : documentList) {
                    KbResult kbResult = new KbResult(document.getText(), DocumentUtil.extractMetaDataValue(document, "author"), DocumentUtil.extractMetaDataValue(document, "microservice"), DocumentUtil.extractMetaDataValue(document, "timestamp"), DocumentUtil.extractMetaDataValue(document, "classification"));
                    result.add(kbResult);
                }
            }
            result.sort(Comparator.comparing(KbResult::timestamp));
            List<KbResult> summaryResult = summaryService.generateResult(result);
            if (!summaryResult.isEmpty()) {
                return summaryResult;
            } else if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            logger.error("Error in searching query {}", e.getMessage());
        } finally {
            if (result.isEmpty()) {
                logger.error("No results found");
            }
        }
        return List.of(KbResult.defaultResult());
    }

    private SearchRequest getSearchRequest(KbSearchRequest kbSearchRequest) {
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
