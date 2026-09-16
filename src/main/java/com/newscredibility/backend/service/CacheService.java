package com.newscredibility.backend.service;

import com.newscredibility.backend.dto.AnalysisResponseDTO;
import com.newscredibility.backend.entity.AnalysisCache;
import com.newscredibility.backend.entity.AnalysisHistory;
import com.newscredibility.backend.entity.User;
import com.newscredibility.backend.repository.AnalysisCacheRepository;
import com.newscredibility.backend.repository.AnalysisHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final AnalysisCacheRepository cacheRepository;
    private final AnalysisHistoryRepository historyRepository;

    // 캐시 조회
    public Optional<AnalysisResponseDTO> findCache(String textHash) {
        return cacheRepository.findByTextHash(textHash)
                .map(cache -> new AnalysisResponseDTO(
                        cache.getExaggeration(),
                        cache.getAiProb(),
                        cache.getLabel(),
                        cache.getCredibility(),
                        cache.getPressName(),
                        null,
                        cache.getFinalScore(),
                        true // 캐시에서 반환
                ));
    }

    // 캐시 저장
    public void saveCache(String textHash, AnalysisResponseDTO result) {
        AnalysisCache cache = AnalysisCache.builder()
                .textHash(textHash)
                .exaggeration(result.getExaggeration())
                .aiProb(result.getAi_prob())
                .label(result.getLabel())
                .credibility(result.getCredibility())
                .pressName(result.getPress_name())
                .finalScore(result.getFinal_score())
                .build();

        cacheRepository.save(cache);
    }

    // 이력 저장
    public void saveHistory(String textHash, AnalysisResponseDTO result, User user) {
        AnalysisHistory history = AnalysisHistory.builder()
                .user(user)
                .textHash(textHash)
                .finalScore(result.getFinal_score())
                .label(result.getLabel())
                .pressName(result.getPress_name())
                .build();

        historyRepository.save(history);
    }
}