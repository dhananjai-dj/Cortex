package com.project.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class SessionAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "document_id")
    private UUID documentId;
    @Column(name = "author")
    private String author;
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
    @Column(name = "microservice")
    private String microservice;
    @Column(name = "classification")
    private String classification;
    @Column(name = "is_retry")
    private boolean isRetry;
    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "is_injected")
    private boolean isInjected;

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
    }

    public Timestamp getTimestamp() {
        return createTime;
    }

    public void setTimestamp(Timestamp createTime) {
        this.createTime = createTime;
    }

    public boolean isInjected() {
        return isInjected;
    }

    public void setInjected(boolean injected) {
        isInjected = injected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMicroservice() {
        return microservice;
    }

    public void setMicroservice(String microservice) {
        this.microservice = microservice;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public boolean getIsRetry() {
        return isRetry;
    }

    public void setIsRetry(boolean isRetry) {
        this.isRetry = isRetry;
    }
}
