package com.newscredibility.backend.controller;

import com.newscredibility.backend.dto.AnalysisRequestDTO;
import com.newscredibility.backend.dto.AnalysisResponseDTO;
import com.newscredibility.backend.dto.FrontAnalysisResponseDTO;
import com.newscredibility.backend.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/analyze")
    public ResponseEntity<FrontAnalysisResponseDTO> analyze(@Valid @RequestBody AnalysisRequestDTO request) {
        AnalysisResponseDTO result = analysisService.analyze(request);
        return ResponseEntity.ok(FrontAnalysisResponseDTO.from(result)); // ② 변환해서 응답
    }
}