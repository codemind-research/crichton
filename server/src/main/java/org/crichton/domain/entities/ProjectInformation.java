package org.crichton.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;
import org.crichton.models.report.InjectorPluginReport;
import org.crichton.models.report.UnitTestPluginReport;
import runner.dto.RunResult;

import java.util.UUID;

@Builder
@Getter
public class ProjectInformation {

    private UUID id;

    private ProjectStatus status;

    private TestResult testResult;

    private String failReason;

    private UUID pluginProcessorId;

    private String sourceDirectoryPath;

    private String injectTestDirectoryPath;

    private String unitTestDirectoryPath;

    @Setter
    private InjectorPluginReport injectorPluginReport;

    @Setter
    private UnitTestPluginReport unitTestPluginReport;


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
