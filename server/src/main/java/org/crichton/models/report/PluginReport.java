package org.crichton.models.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
public abstract class PluginReport {
    private final String pluginName;
}
