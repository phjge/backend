package com.newscredibility.backend.controller;

import com.newscredibility.backend.dto.AnalysisRequestDTO;
import com.newscredibility.backend.dto.AnalysisResponseDTO;
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
    public ResponseEntity<AnalysisResponseDTO> analyze(@Valid @RequestBody AnalysisRequestDTO request) {
        return ResponseEntity.ok(analysisService.analyze(request));
    }
}