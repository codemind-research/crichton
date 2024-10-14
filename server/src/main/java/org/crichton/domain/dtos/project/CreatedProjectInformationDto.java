package org.crichton.domain.dtos.project;

import lombok.Getter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;

import java.util.UUID;

@Getter
public class CreatedProjectInformationDto {
    private UUID uuid = UUID.randomUUID();
    private ProjectStatus projectStatus = ProjectStatus.None;
    private TestResult testResult = TestResult.None;
    private String failReason = null;
}
