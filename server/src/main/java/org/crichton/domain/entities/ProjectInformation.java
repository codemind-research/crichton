package org.crichton.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProjectInformation {

    private UUID id;

    private ProjectStatus status;

    private TestResult testResult;

    private String failReason;

    @Builder
    public ProjectInformation(UUID id, ProjectStatus status, TestResult testResult, String failReason) {
        this.id = (id != null) ? id : UUID.randomUUID(); // UUID가 null이면 자동 생성
        this.status = (status != null) ? status : ProjectStatus.None;
        this.testResult = (testResult != null) ? testResult : TestResult.None;
        this.failReason = (failReason != null) ? failReason : "";
    }

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
