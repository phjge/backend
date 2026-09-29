package com.newscredibility.backend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class AiAnalyzeResponse {
    private Double exaggeration;

    @JsonProperty("ai_prob")
    private Double aiProb;

    private String label;

    private Double credibility;

    @JsonProperty("press_name")
    private String pressName;

    @JsonProperty("top_articles")
    private List<Map<String, Object>> topArticles;

    @JsonProperty("final_score")
    private Double finalScore;
}
