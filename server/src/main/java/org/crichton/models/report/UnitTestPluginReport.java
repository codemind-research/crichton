package org.crichton.models.report;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.parameters.P;

import java.util.List;

@SuperBuilder
@Getter
public class UnitTestPluginReport extends PluginReport {
    private List<UnitTestReport> reports;
}
