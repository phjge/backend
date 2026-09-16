package com.newscredibility.backend.client;

import com.newscredibility.backend.dto.AnalysisRequestDTO;
import com.newscredibility.backend.dto.AnalysisResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
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
        ResponseEntity<AnalysisResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                AnalysisResponseDTO.class
        );

        return response.getBody();
    }
}