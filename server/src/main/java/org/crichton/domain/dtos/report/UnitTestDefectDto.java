package org.crichton.domain.dtos.report;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class UnitTestDefectDto {

    private String file;
    private String functionName;
    private String defectCode;
}
