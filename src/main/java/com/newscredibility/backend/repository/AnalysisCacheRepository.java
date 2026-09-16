package com.newscredibility.backend.repository;

import com.newscredibility.backend.entity.AnalysisCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisCacheRepository extends JpaRepository<AnalysisCache, Long> {
    Optional<AnalysisCache> findByTextHash(String textHash);
}