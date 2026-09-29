package com.newscredibility.backend.dto;

import com.newscredibility.backend.entity.AnalysisHistory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryItemDTO {

    private Long id;
    private String inputText;
    private int totalScore;
    private String label;
    private String pressName;
    private String analyzedAt;

    public static HistoryItemDTO from(AnalysisHistory h) {
        return HistoryItemDTO.builder()
                .id(h.getId())
                .inputText(h.getInputText())
                .totalScore(h.getFinalScore() == null ? 0 : Math.round(h.getFinalScore()))
                .label(h.getLabel())
                .pressName(h.getPressName())
                .analyzedAt(h.getCreatedAt() == null ? null : h.getCreatedAt().toString())
                .build();
    }
}
