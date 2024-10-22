package org.crichton.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;
import org.crichton.models.defect.DefectSpec;

import java.util.List;
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

    private UUID pluginProcessorId;

    @Setter
    private List<DefectSpec> defectSpecs;


    public void updateStatus(ProjectStatus status) {
        this.status = status;
    }

    public void updateTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public void updateFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void updatePluginProcessorId(UUID pluginProcessorId) {
        this.pluginProcessorId = pluginProcessorId;
    }
}
