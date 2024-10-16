package org.crichton.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class ProjectInformation {

    private UUID id;

    private List<TestSpec.Task> tasks;

    private ProjectStatus status;

    private TestResult testResult;

    private String failReason;


    public void updateStatus(ProjectStatus status) {
        this.status = status;
    }

    public void updateTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public void updateFailReason(String failReason) {
        this.failReason = failReason;
    }
}
