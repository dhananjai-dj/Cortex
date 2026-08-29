package com.project.controller;

import com.project.dao.MergeHistory;
import com.project.dao.SessionAudit;
import com.project.service.MergeHistoryService;
import com.project.service.SessionAuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/dashboard")
public class DashBoardController {

    private final Logger logger = LoggerFactory.getLogger(DashBoardController.class);

    private final SessionAuditService sessionAuditService;
    private final MergeHistoryService mergeHistoryService;

    public DashBoardController(SessionAuditService sessionAuditService, MergeHistoryService mergeHistoryService) {
        this.sessionAuditService = sessionAuditService;
        this.mergeHistoryService = mergeHistoryService;
    }

    @GetMapping("/merge-history")
    public ResponseEntity<?> getMergeHistory(HttpServletRequest request) {
        ResponseEntity<?> responseEntity = null;
        try {
            List<MergeHistory> mergeHistories = mergeHistoryService.getAllMergeHistory();
            responseEntity = new ResponseEntity<>(mergeHistories, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getting merge history {}", e.getMessage());
        }
        return responseEntity;
    }

    @GetMapping("/session-audit")
    public ResponseEntity<?> getSessionAudit(HttpServletRequest request) {
        ResponseEntity<?> responseEntity = null;
        try {
            List<SessionAudit> sessionAudits = sessionAuditService.getAllSessionAudit();
            responseEntity = new ResponseEntity<>(sessionAudits, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getting complete session audit {}", e.getMessage());
        }
        return responseEntity;
    }

    @GetMapping("/session-audit/microservice/{microservice_name}")
    public ResponseEntity<?> getSessionAuditMicroservice(HttpServletRequest request, @PathVariable("microservice_name") String microserviceName) {
        ResponseEntity<?> responseEntity = null;
        try {
            List<SessionAudit> sessionAudits = sessionAuditService.getSessionAuditByMicroservice(microserviceName);
            responseEntity = new ResponseEntity<>(sessionAudits, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getting session audit by microservice {}", e.getMessage());
        }
        return responseEntity;
    }
}
