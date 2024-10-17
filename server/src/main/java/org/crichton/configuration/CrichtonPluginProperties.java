package org.crichton.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crichton.util.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.Optional;

@Getter
@ConfigurationProperties(prefix = "crichton.plugin")
@Slf4j
@RequiredArgsConstructor
public class CrichtonPluginProperties {

    private static final String DEFAULT_TRAMPOLINE_PATH = System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "trampoline";

    private static final String DEFAULT_INJECTOR_PLUGIN_PATH = System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "plugins" + File.separator + "injector";

    private static final String DEFAULT_UNIT_TESTER_PLUGIN_PATH = System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "plugins" + File.separator + "coyote";

    private final Optional<String> trampolinePath;

    private final Optional<String> injectorPath;

    private final Optional<String> unitTesterPath;

    public String getTrampolinePath() {
        var path = trampolinePath.orElse(DEFAULT_TRAMPOLINE_PATH);
        return FileUtils.getAbsolutePath(path);
    }

    public String getInjectorPath() {
        var path = injectorPath.orElse(DEFAULT_INJECTOR_PLUGIN_PATH);
        return FileUtils.getAbsolutePath(path);
    }

    public String getUnitTesterPath() {
        var path = unitTesterPath.orElse(DEFAULT_UNIT_TESTER_PLUGIN_PATH);
        return FileUtils.getAbsolutePath(path);
    }

}
