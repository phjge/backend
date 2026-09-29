package com.newscredibility.backend.client;

import com.newscredibility.backend.dto.AnalysisRequestDTO;
import com.newscredibility.backend.dto.AnalysisResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FastApiClient {

    private final RestTemplate restTemplate;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public AnalysisResponseDTO requestAnalysis(String text) {
        String url = fastApiUrl + "/ai/analyze";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("text", text);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        // FastAPI 호출
        try {
            ResponseEntity<AnalysisResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, AnalysisResponseDTO.class   // ← 여기도 변경
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("FastAPI 호출 실패: {}", e.getMessage());
            throw new IllegalStateException("AI 서버에 연결할 수 없습니다.", e);
        }
    }
}