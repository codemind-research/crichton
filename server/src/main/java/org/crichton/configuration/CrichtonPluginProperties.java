package org.crichton.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.Optional;

@Getter
@ConfigurationProperties(prefix = "crichton.plugin")
@Slf4j
@RequiredArgsConstructor
public class CrichtonPluginProperties {

    private final Optional<String> trampolinePath;

    private final Optional<String> defectInjectorPath;

    private final Optional<String> injectionTesterPath;

    public String getTrampolinePath() {
        return trampolinePath.orElse(System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "trampoline");
    }

    public String getDefectInjectorPath() {
        return defectInjectorPath.orElse(System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "defectInjector");
    }

    public String getInjectionTesterPath() {
        return injectionTesterPath.orElse(System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "injectionTester");
    }

}
