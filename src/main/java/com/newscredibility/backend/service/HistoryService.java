package com.newscredibility.backend.service;

import com.newscredibility.backend.dto.HistoryItemDTO;
import com.newscredibility.backend.entity.AnalysisHistory;
import com.newscredibility.backend.entity.User;
import com.newscredibility.backend.repository.AnalysisHistoryRepository;
import com.newscredibility.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final AnalysisHistoryRepository historyRepository;
    private final UserRepository userRepository;

    /** 내 분석 기록 (최신순, 페이지 단위) */
    public Page<HistoryItemDTO> getMyHistory(int page, int size) {
        User user = currentUser();
        return historyRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(HistoryItemDTO::from);
    }

    /** 내 기록 삭제 (본인 것만) */
    @Transactional
    public void deleteMyHistory(Long id) {
        User user = currentUser();
        AnalysisHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("기록을 찾을 수 없습니다."));

        if (!history.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인 기록만 삭제할 수 있습니다.");
        }
        historyRepository.delete(history);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}
