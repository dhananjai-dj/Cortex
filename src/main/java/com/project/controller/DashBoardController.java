package com.project.controller;

import com.project.dao.MergeHistory;
import com.project.dao.SessionAudit;
import com.project.service.MergeHistoryService;
import com.project.service.SessionAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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
    public ResponseEntity<?> getMergeHistory(@RequestParam int page, @RequestParam int size) {
        ResponseEntity<?> responseEntity = null;
        try {
            List<MergeHistory> mergeHistories = mergeHistoryService.getAllMergeHistory(page, size);
            responseEntity = new ResponseEntity<>(mergeHistories, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getting merge history {}", e.getMessage());
        }
        return responseEntity;
    }

    @GetMapping("/session-audit/{key}/{value}")
    public ResponseEntity<?> getSessionAudit(@PathVariable("key") String key, @PathVariable("value") String value, @RequestParam int page, @RequestParam int size) {
        ResponseEntity<?> responseEntity = null;
        try {
            List<SessionAudit> sessionAudits = switch (key) {
                case "microservice" -> sessionAuditService.getSessionAuditByMicroservice(value, page, size);
                case "author" -> sessionAuditService.getSessionAuditByAuthor(value, page, size);
                default -> sessionAuditService.getAllSessionAudit(page, size);
            };
            responseEntity = new ResponseEntity<>(sessionAudits, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getting session audit {}", e.getMessage());
        }
        return responseEntity;
    }

    @GetMapping("/session-audit/date")
    public ResponseEntity<?> getSessionAuditByDateRange(@RequestParam Timestamp startDate, @RequestParam Timestamp endDate, @RequestParam int page, @RequestParam int size) {
        ResponseEntity<?> responseEntity = null;
        try {
            List<SessionAudit> sessionAudits = sessionAuditService.getSessionAuditByDate(startDate, endDate, page, size);
            responseEntity = new ResponseEntity<>(sessionAudits, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in getting the session audit by date range {}", e.getMessage());
        }
        return responseEntity;
    }

}
