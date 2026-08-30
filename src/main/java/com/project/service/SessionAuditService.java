package com.project.service;

import com.project.dao.SessionAudit;
import com.project.dao.repository.SessionAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionAuditService {
    Logger logger = LoggerFactory.getLogger(SessionAuditService.class);
    private final SessionAuditRepository sessionAuditRepository;

    public SessionAuditService(SessionAuditRepository sessionAuditRepository) {
        this.sessionAuditRepository = sessionAuditRepository;
    }

    private boolean save(SessionAudit sessionAudit) {
        boolean result = false;
        try {
            sessionAuditRepository.save(sessionAudit);
            result = true;
        } catch (Exception e) {
            logger.error("Error in saving session audit");
        }
        return result;
    }

    public void createSessionAudit(String summary, UUID documentId, Map<String, Object> metadata, boolean isRetry, boolean isInjected) {
        try {
            SessionAudit sessionAudit = new SessionAudit();
            sessionAudit.setIsRetry(isRetry);
            sessionAudit.setInjected(isInjected);
            sessionAudit.setSummary(summary);
            sessionAudit.setDocumentId(documentId);
            sessionAudit.setAuthor(extractMetadataValue(metadata, "author"));
            sessionAudit.setMicroservice(extractMetadataValue(metadata, "microservice"));
            sessionAudit.setClassification(extractMetadataValue(metadata, "classification"));

            if (save(sessionAudit)) {
                logger.debug("Created session audit for document {}", documentId);
            } else {
                logger.error("Error in creating session audit for document {}", documentId);
            }
        } catch (Exception e) {
            logger.error("Error in creating session audit for document {}: {}", documentId, e.getMessage());
        }
    }

    private String extractMetadataValue(Map<String, Object> metadata, String key) {
        Object value = metadata != null ? metadata.get(key) : null;
        return value != null ? value.toString() : "UNKNOWN";
    }


    public List<SessionAudit> getAllSessionAudit(int page, int size) {
        List<SessionAudit> sessionAudits = null;
        try {
            Pageable pageable = PageRequest.of(page, size);
            sessionAudits = sessionAuditRepository.findAll(pageable).stream().toList();
            return sessionAudits;
        } catch (Exception e) {
            logger.error("Error in getting complete session audit from DB {}", e.getMessage());
        }
        return sessionAudits;
    }

    public List<SessionAudit> getSessionAuditByMicroservice(String microservice, int page, int size) {
        List<SessionAudit> sessionAudits = null;
        try {
            Pageable pageable = PageRequest.of(page, size);
            sessionAudits = sessionAuditRepository.findByMicroservice(microservice, pageable);
            return sessionAudits;
        } catch (Exception e) {
            logger.error("Error in getting session audit by microservice from DB {}", e.getMessage());
        }
        return sessionAudits;
    }


    public List<SessionAudit> getSessionAuditByAuthor(String author, int page, int size) {
        List<SessionAudit> sessionAudits = null;
        try {
            Pageable pageable = PageRequest.of(page, size);
            sessionAudits = sessionAuditRepository.findByAuthor(author, pageable);
            return sessionAudits;
        } catch (Exception e) {
            logger.error("Error in getting session audit by author from DB {}", e.getMessage());
        }
        return sessionAudits;
    }

    public List<SessionAudit> getSessionAuditByDate(Timestamp starDate, Timestamp endDate, int page, int size) {
        List<SessionAudit> sessionAudits = null;
        try {
            Pageable pageable = PageRequest.of(page, size);
            sessionAudits = sessionAuditRepository.findByCreateTimeBetween(starDate, endDate, pageable);
            return sessionAudits;
        } catch (Exception e) {
            logger.error("Error in getting session audit by start date and end date from DB {}", e.getMessage());
        }
        return sessionAudits;
    }


}
