package com.project.service;


import com.project.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.document.Document;

@Service
public class InjectorService {

    private final Logger logger = LoggerFactory.getLogger(InjectorService.class);
    private final ExecutorService injectionExecutor = Executors.newFixedThreadPool(50);
    private final MergeHistoryService mergeHistoryService;
    private final SessionAuditService sessionAuditService;
    private final SummaryService summaryService;
    private final VectorStore vectorStore;

    public InjectorService(MergeHistoryService mergeHistoryService, SessionAuditService sessionAuditService, VectorStore vectorStore, SummaryService summaryService) {
        this.mergeHistoryService = mergeHistoryService;
        this.sessionAuditService = sessionAuditService;
        this.summaryService = summaryService;
        this.vectorStore = vectorStore;
    }

    public void injectData(String summary, Map<String, Object> metadata, boolean isRetry) {
        injectionExecutor.submit(() -> {
            List<String> similarDocumentIds = new ArrayList<>();
            try {
                String combinedSummary = getCombinedSummary(summary, similarDocumentIds);
                String newSummary = summaryService.generateCombinedSummary(combinedSummary, similarDocumentIds);
                UUID documentId = UUID.randomUUID();
                Document document = new Document(documentId.toString(), newSummary, metadata);
                vectorStore.add(List.of(document));
                if (mergeHistoryService.addHistory(similarDocumentIds, documentId)) {
                    logger.debug("Added history for similar documents to merge history");
                }
                sessionAuditService.createSessionAudit(summary, documentId, metadata, isRetry);
            } catch (Exception e) {
                logger.error("Error in injecting data: ", e);
                pushToRetry(summary, metadata);
            }
        });
    }


    private String getCombinedSummary(String summary, List<String> similarDocumentIds) {
        try {
            StringBuilder stringBuilder = new StringBuilder(summary);
            List<Document> documentList = vectorStore.similaritySearch(summary);
            for (Document document : documentList) {
                if (DocumentUtil.isSimilarDocument(document)) {
                    similarDocumentIds.add(document.getId());
                    stringBuilder.append(DocumentUtil.toString(document));
                }
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            logger.error("Error in searching similarity so inserting anyways {}", e.getMessage());
        }
        return summary;
    }

    private void pushToRetry(String summary, Map<String, Object> metadata) {
        //push to kakfa queue
    }

}
