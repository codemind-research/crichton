package org.crichton.domain.dtos.project;

import lombok.Builder;
import lombok.Getter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;

@Getter
@Builder
public class UpdatedProjectInformationDto {
    private ProjectStatus status;
    private TestResult testResult;
    private String failReason;
}
