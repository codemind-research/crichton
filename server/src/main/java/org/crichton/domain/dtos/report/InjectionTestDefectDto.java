package org.crichton.domain.dtos.report;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Locale;

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


    static Schema<?> getSchema(MessageSource messageSource, Locale locale) {
        return new Schema<>()
                .addProperty("file", new Schema<>().type("string").description(messageSource.getMessage("responseReport.injectionTestDefect.file", null, locale)))
                .addProperty("defect_id", new Schema<>().type("integer").description(messageSource.getMessage("responseReport.injectionTestDefect.defectId", null, locale)))
                .addProperty("violation_id", new Schema<>().type("integer").description(messageSource.getMessage("responseReport.injectionTestDefect.violationId", null, locale)));
    }
}
