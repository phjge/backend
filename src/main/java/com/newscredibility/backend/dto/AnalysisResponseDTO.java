package com.newscredibility.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResponseDTO {

    private float exaggeration;        // 과장성 점수
    private float ai_prob;             // AI 생성 의심도
    private String label;              // 분석 라벨
    private float credibility;         // 출처 신뢰도
    private String press_name;         // 언론사 이름
    private List<Object> top_articles; // 유사 기사 목록
    private float final_score;         // 종합 신뢰도 점수
    private boolean cached;            // 캐시 반환 여부
}