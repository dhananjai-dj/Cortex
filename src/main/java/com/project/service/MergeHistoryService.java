package com.project.service;

import com.project.dao.MergeHistory;
import com.project.dao.repository.MergeHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class MergeHistoryService {

    private final MergeHistoryRepository mergeHistoryRepository;
    private final Logger logger = LoggerFactory.getLogger(MergeHistoryService.class.getName());

    public MergeHistoryService(MergeHistoryRepository mergeHistoryRepository) {
        this.mergeHistoryRepository = mergeHistoryRepository;
    }

    public boolean addHistory(List<String> documentIds, UUID newDocumentId) {
        try {
            List<MergeHistory> mergeHistories = new ArrayList<>();
            for (String documentId : documentIds) {
                MergeHistory mergeHistory = new MergeHistory();
                mergeHistory.setOldDocumentId(UUID.fromString(documentId));
                mergeHistory.setNewDocumentId(newDocumentId);
                mergeHistories.add(mergeHistory);
            }
            return saveMergeHistoryBatch(mergeHistories);
        } catch (Exception e) {
            logger.error("Error adding history", e);
        }
        return false;
    }

    private boolean saveMergeHistoryBatch(List<MergeHistory> mergeHistory) {
        boolean result = false;
        try {
            mergeHistoryRepository.saveAll(mergeHistory);
            result = true;
        } catch (Exception e) {
            logger.error("Error in saving merge history in bulk", e);
        }
        return result;
    }

    private boolean saveMergeHistory(MergeHistory mergeHistory) {
        boolean result = false;
        try {
            mergeHistoryRepository.save(mergeHistory);
            result = true;
        } catch (Exception e) {
            logger.error("Error saving merge history", e);
        }
        return result;
    }

    public List<MergeHistory> getAllMergeHistory(int page, int size) {
        List<MergeHistory> mergeHistories = null;
        try {
            Pageable pageable = PageRequest.of(page, size);
            mergeHistories = mergeHistoryRepository.findAll(pageable).stream().toList();
        } catch (Exception e) {
            logger.error("Error getting all merge history", e);
        }
        return mergeHistories;
    }
}
