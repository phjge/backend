package com.newscredibility.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnalysisRequestDTO {

    @NotBlank(message = "분석할 텍스트를 입력해주세요.")
    @Size(max = 5000, message = "텍스트는 5000자 이하여야 합니다.")
    private String text;
}