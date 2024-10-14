package org.crichton.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.crichton.domain.utils.converters.ProjectStatusEnumToStringConverter;
import org.crichton.domain.utils.converters.TestResultEnumToStringConverter;
import org.crichton.domain.utils.converters.UUIDToStringConverter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@DynamicUpdate
public class ProjectInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = UUIDToStringConverter.class)
    private UUID uuid;

    @Convert(converter = ProjectStatusEnumToStringConverter.class)
    private ProjectStatus status;

    @Convert(converter = TestResultEnumToStringConverter.class)
    private TestResult testResult;

    @Column(columnDefinition = "TEXT", nullable = false)
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
