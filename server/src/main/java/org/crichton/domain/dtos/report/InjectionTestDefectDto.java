package org.crichton.domain.dtos.report;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InjectionTestDefectDto {

    // 파일 경로
    private String file;

    // 결함 주입 스펙상의 id
    @JsonProperty("defect_id")
    private int defectId;

    // 안정 판정 스펙상의 id
    @JsonProperty("violation_id")
    private int violationId;
}
