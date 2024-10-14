package org.crichton.domain.dtos.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ResponseReportDto {

    @Builder.Default
    @JsonProperty("injection-test-defects")
    private List<InjectionTestDefectDto> injectionTestDefects = new ArrayList<>();

    @Builder.Default
    @JsonProperty("unit-test-defects")
    private List<UnitTestDefectDto> unitTestDefects = new ArrayList<>();
}
