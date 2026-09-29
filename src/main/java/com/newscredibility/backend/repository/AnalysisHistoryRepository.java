package com.newscredibility.backend.repository;

import com.newscredibility.backend.entity.AnalysisHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Long> {
    List<AnalysisHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 기록 페이지용: 10개씩 끊어서 조회
    Page<AnalysisHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}