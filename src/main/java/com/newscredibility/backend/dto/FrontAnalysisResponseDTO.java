package com.newscredibility.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class FrontAnalysisResponseDTO {
    private int totalScore;
    private Metrics metrics;
    private List<SimilarArticle> similarArticles;
    private List<String> reasoning;
    private String analyzedAt;

    @Getter
    @Builder
    public static class Metrics {
        private int sourceCredibility;
        private int exaggeration;
        private int aiSuspicion;
    }

    @Getter
    @Builder
    public static class SimilarArticle {
        private int id;
        private String title;
        private String source;
        private String url;
        private String publishedAt;
        private int similarity;
    }

    /** AI 서버 형식 → 프론트 형식 변환 */
    public static FrontAnalysisResponseDTO from(AnalysisResponseDTO ai) {
        int credibility  = toPercent(ai.getCredibility());      // DSD 기준 0~1 → 0~100
        int exaggeration = Math.round(ai.getExaggeration());     // 테스트 결과 이미 0~100
        int aiSuspicion  = Math.round(ai.getAi_prob());          // 테스트 결과 이미 0~100

        return FrontAnalysisResponseDTO.builder()
                .totalScore(Math.round(ai.getFinal_score()))
                .metrics(Metrics.builder()
                        .sourceCredibility(credibility)
                        .exaggeration(exaggeration)
                        .aiSuspicion(aiSuspicion)
                        .build())
                .similarArticles(toArticles(ai.getTop_articles()))
                .reasoning(makeReasoning(credibility, exaggeration, aiSuspicion))
                .analyzedAt(Instant.now().toString())
                .build();
    }

    /** 유사 기사 변환 (AI 쪽 필드명이 확정되면 키 이름만 맞추면 됨) */
    private static List<SimilarArticle> toArticles(List<Object> topArticles) {
        List<SimilarArticle> result = new ArrayList<>();
        if (topArticles == null) return result;

        int id = 1;
        for (Object item : topArticles) {
            if (!(item instanceof Map<?, ?> m)) continue;
            Object press = m.get("press") != null ? m.get("press") : m.get("source");

            result.add(SimilarArticle.builder()
                    .id(id++)
                    .title(str(m.get("title")))
                    .source(str(press))
                    .url(str(m.get("url")))
                    .publishedAt(str(m.get("pubDate")))
                    .similarity(toPercent(num(m.get("similarity"))))
                    .build());
        }
        return result;
    }

    /** 점수 기반 판단 근거 문장 생성 */
    private static List<String> makeReasoning(int credibility, int exaggeration, int aiSuspicion) {
        List<String> list = new ArrayList<>();

        if (credibility >= 70)      list.add("공신력 있는 언론사에서 보도가 확인됩니다.");
        else if (credibility == 0)  list.add("유사한 보도를 찾지 못해 출처를 확인하기 어렵습니다.");
        else                        list.add("출처 신뢰도가 낮은 편입니다.");

        if (exaggeration < 40)      list.add("텍스트 표현이 비교적 중립적입니다.");
        else if (exaggeration < 60) list.add("일부 과장된 표현이 포함되어 있습니다.");
        else                        list.add("자극적이거나 과장된 표현이 다수 포함되어 있습니다.");

        if (aiSuspicion < 40)       list.add("AI 생성 텍스트 패턴이 크게 감지되지 않습니다.");
        else if (aiSuspicion < 60)  list.add("AI 생성 여부를 판단하기 어렵습니다.");
        else                        list.add("AI가 생성한 텍스트일 가능성이 있습니다.");

        return list;
    }

    /** 0~1 값이면 0~100으로 변환 */
    private static int toPercent(float value) {
        return Math.round(value <= 1.0f ? value * 100 : value);
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }

    private static float num(Object o) {
        return o instanceof Number n ? n.floatValue() : 0f;
    }
}
