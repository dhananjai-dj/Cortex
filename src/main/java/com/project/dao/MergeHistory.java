package com.project.dao;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "merge_history")
public class MergeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "old_document_id")
    private UUID oldDocumentId;

    @Column(name = "new_document_id")
    private UUID newDocumentId;

    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOldDocumentId() {
        return oldDocumentId;
    }

    public void setOldDocumentId(UUID oldDocumentId) {
        this.oldDocumentId = oldDocumentId;
    }

    public UUID getNewDocumentId() {
        return newDocumentId;
    }

    public void setNewDocumentId(UUID newDocumentId) {
        this.newDocumentId = newDocumentId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
