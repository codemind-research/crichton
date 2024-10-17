package org.crichton.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;

import java.util.UUID;

@Builder
@Getter
public class ProjectInformation {

    private UUID id;

    @Setter
    private TestSpec testSpec;

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
