package com.newscredibility.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_cache")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text_hash", unique = true, nullable = false)
    private String textHash;

    private Float exaggeration;

    @Column(name = "ai_prob")
    private Float aiProb;

    private String label;

    private Float credibility;

    @Column(name = "press_name")
    private String pressName;

    @Column(name = "final_score")
    private Float finalScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}