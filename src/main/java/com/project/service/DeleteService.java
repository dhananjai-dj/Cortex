package com.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeleteService {
    private final Logger logger = LoggerFactory.getLogger(DeleteService.class);
    private final VectorStore vectorStore;


    public DeleteService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void deleteData(List<String> documentIds) {
        try {
            vectorStore.delete(documentIds);
        } catch (Exception e) {
            logger.error("Error in deleting documents");
        }
    }
}
