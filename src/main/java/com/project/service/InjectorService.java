package com.project.service;


import com.project.dto.InjectRequest;
import com.project.kafka.Producer;
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
    private final Producer producer;

    public InjectorService(MergeHistoryService mergeHistoryService, SessionAuditService sessionAuditService, VectorStore vectorStore, SummaryService summaryService, Producer producer) {
        this.mergeHistoryService = mergeHistoryService;
        this.sessionAuditService = sessionAuditService;
        this.summaryService = summaryService;
        this.vectorStore = vectorStore;
        this.producer = producer;
    }

    public void injectData(String summary, Map<String, Object> metadata) {
        injectionExecutor.submit(() -> {
            UUID documentId = UUID.randomUUID();
            try {
                process(documentId, summary, metadata, false);
            } catch (Exception e) {
                logger.error("Error in processing injection {} ", e.getMessage());
                pushToRetry(documentId.toString(), summary, metadata);
            }
        });
    }

    public void process(UUID documentId, String summary, Map<String, Object> metaData, boolean isRetry) {
        List<String> similarDocumentIds = new ArrayList<>();
        try {
            String combinedSummary = getCombinedSummary(summary, similarDocumentIds);
            String newSummary = summaryService.generateCombinedSummary(combinedSummary, similarDocumentIds);
            Document document = new Document(documentId.toString(), newSummary, metaData);
            vectorStore.add(List.of(document));
            if (mergeHistoryService.addHistory(similarDocumentIds, documentId)) {
                logger.debug("Added history for similar documents to merge history");
            }
            sessionAuditService.createSessionAudit(summary, documentId, metaData, isRetry);
        } catch (Exception e) {
            logger.error("Error in injecting data: {}", e.getMessage());
            throw new RuntimeException("Error in injecting data");
        }
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

    private void pushToRetry(String documentId, String summary, Map<String, Object> metadata) {
        try {
            InjectRequest injectRequest = new InjectRequest(summary, metadata);
            producer.pushToRetry(documentId, injectRequest);
        } catch (Exception e) {
            logger.error("Error in pushing to retry for the document Id {}", documentId);
        }
    }


}
