package org.crichton.models.report;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class InjectorPluginReport extends PluginReport {
    private List<InjectorDefectReport> reports;
}
