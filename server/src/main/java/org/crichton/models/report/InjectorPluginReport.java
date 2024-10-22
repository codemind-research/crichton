package org.crichton.models.report;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class InjectorPluginReport {
    private String pluginName;
    private List<DefectReport> defectReports;
}
