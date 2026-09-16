package com.newscredibility.backend.service;

import com.newscredibility.backend.client.FastApiClient;
import com.newscredibility.backend.dto.AnalysisRequestDTO;
import com.newscredibility.backend.dto.AnalysisResponseDTO;
import com.newscredibility.backend.entity.User;
import com.newscredibility.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final CacheService cacheService;
    private final FastApiClient fastApiClient;
    private final UserRepository userRepository;

    public AnalysisResponseDTO analyze(AnalysisRequestDTO request) {

        String textHash = hashText(request.getText());

        // 1. 캐시 확인
        Optional<AnalysisResponseDTO> cached = cacheService.findCache(textHash);
        if (cached.isPresent()) {
            return cached.get();
        }

        // 2. FastAPI 호출
        AnalysisResponseDTO result = fastApiClient.requestAnalysis(request.getText());

        // 3. 캐시 저장
        cacheService.saveCache(textHash, result);

        // 4. 로그인한 사용자 이력 저장
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByEmail(email).ifPresent(user ->
                cacheService.saveHistory(textHash, result, user)
        );

        return result;
    }

    // SHA-256 해시
    private String hashText(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("텍스트 해시 생성 실패", e);
        }
    }
}