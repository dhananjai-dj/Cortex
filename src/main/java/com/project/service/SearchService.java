package com.project.service;

import com.project.dto.KbResult;
import com.project.dto.SearchRequest;
import com.project.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
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

    public List<KbResult> searchData(SearchRequest searchRequest) {
        String query = searchRequest.query();
        List<KbResult> result = new ArrayList<>();
        try {
            List<Document> documentList = vectorStore.similaritySearch(query);
            List<Document> validDocuments = getValidDocuments(searchRequest, documentList);
            if (DocumentUtil.isNonEmptyDocumentList(validDocuments)) {
                for (Document document : validDocuments) {
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
            logger.error("Error in searching data {}", e.getMessage());
        } finally {
            if (result.isEmpty()) {
                logger.error("No results found");
            }
        }
        return List.of(new KbResult("No Content found", "", "", "", ""));
    }

    private List<Document> getValidDocuments(SearchRequest searchRequest, List<Document> documentList) {
        try {
            List<Document> validDocuments = new ArrayList<>();
            int limit = searchRequest.limit() != null ? Math.max(5, searchRequest.limit()) : 5;
            double minScore = searchRequest.minScore() != null ? Math.min(DocumentUtil.getSimilarBoundaryScore(), searchRequest.minScore()) : DocumentUtil.getSimilarBoundaryScore();
            for (Document document : documentList) {
                if (DocumentUtil.getDocumentScore(document) > minScore) {
                    if (limit == 0) {
                        break;
                    }
                    validDocuments.add(document);
                    limit--;
                }
            }
            return validDocuments;
        } catch (Exception e) {
            logger.error("Error in validating the documents {}", e.getMessage());
            logger.info("Returning all Documents since there is error in validating");
        }
        return documentList;
    }
}
