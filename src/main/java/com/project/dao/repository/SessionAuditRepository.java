package com.project.dao.repository;

import com.project.dao.SessionAudit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface SessionAuditRepository extends JpaRepository<SessionAudit, Long> {
    List<SessionAudit> findByMicroservice(String microservice, Pageable pageable);

    List<SessionAudit> findByAuthor(String author, Pageable pageable);

    List<SessionAudit> findByCreateTimeBetween(Timestamp starDate, Timestamp endDate, Pageable pageable);
}
