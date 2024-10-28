package org.crichton.domain.dtos.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Builder
public class ResponseReportDto {

    @Builder.Default
    @JsonProperty("injection-test-defects")
    private List<InjectionTestDefectDto> injectionTestDefects = new ArrayList<>();

    @Builder.Default
    @JsonProperty("unit-test-defects")
    private List<UnitTestDefectDto> unitTestDefects = new ArrayList<>();

    public static Schema<?> getSchema(MessageSource messageSource, Locale locale) {
        return new Schema<>()
                .addProperty("injection-test-defects", new ArraySchema()
                        .items(InjectionTestDefectDto.getSchema(messageSource, locale))
                        .description(messageSource.getMessage("responseReport.injectionTestDefects", null, locale)))
                .addProperty("unit-test-defects", new ArraySchema()
                        .items(UnitTestDefectDto.getSchema(messageSource, locale))
                        .description(messageSource.getMessage("responseReport.unitTestDefects", null, locale)));
    }
}
