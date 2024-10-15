package org.crichton.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.crichton.domain.utils.converters.ProjectStatusEnumToStringConverter;
import org.crichton.domain.utils.converters.TestResultEnumToStringConverter;
import org.crichton.domain.utils.converters.UUIDToStringConverter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;
import org.crichton.util.constants.EntityCode;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProjectInformation {

    @Setter
    private Long id = EntityCode.UNDEFINED;

    private UUID uuid;

    private ProjectStatus status;

    private TestResult testResult;

    private String failReason;

    @Builder
    public ProjectInformation(UUID uuid, ProjectStatus status, TestResult testResult, String failReason) {
        this.uuid = (uuid != null) ? uuid : UUID.randomUUID(); // UUID가 null이면 자동 생성
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
