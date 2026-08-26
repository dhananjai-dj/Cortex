package com.project.dao.repository;

import com.project.dao.MergeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MergeHistoryRepository extends JpaRepository<MergeHistory, UUID> {
}
