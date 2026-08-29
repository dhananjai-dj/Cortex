package com.project.service;

import com.project.dao.SessionAudit;
import com.project.dao.repository.SessionAuditRepository;
import com.project.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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


    public void createSessionAudit(String summary, UUID documentId, Map<String, Object> metadata, boolean isRetry) {
        SessionAudit sessionAudit = null;
        try {
            sessionAudit = new SessionAudit();
            sessionAudit.setIsRetry(isRetry);
            sessionAudit.setSummary(DocumentUtil.encodeString(summary));
            sessionAudit.setAuthor(metadata.get("author").toString());
            sessionAudit.setClassification(metadata.get("classification").toString());
            sessionAudit.setMicroservice(metadata.get("microservice").toString());
            sessionAudit.setDocumentId(documentId);
            if (save(sessionAudit)) {
                logger.debug("Created session audit for similar documents");
            } else {
                logger.error("Error in creating session audit for similar documents");
            }
        } catch (Exception e) {
            logger.error("Error in creating session audit {}", e.getMessage());
        }
    }


    public List<SessionAudit> getAllSessionAudit() {
        List<SessionAudit> sessionAudits = null;
        try {
            sessionAudits = sessionAuditRepository.findAll();
            return sessionAudits;
        } catch (Exception e) {
            logger.error("Error in getting complete session audit from DB {}", e.getMessage());
        }
        return sessionAudits;
    }

    public List<SessionAudit> getSessionAuditByMicroservice(String microservice) {
        List<SessionAudit> sessionAudits = null;
        try {
            sessionAudits = sessionAuditRepository.findByMicroservice(microservice);
            return sessionAudits;
        } catch (Exception e) {
            logger.error("Error in getting session audit by microservice from DB {}", e.getMessage());
        }
        return sessionAudits;
    }
}
