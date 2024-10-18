package org.crichton.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crichton.util.FileUtils;
import org.crichton.util.constants.FileName;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.Optional;

@Getter
@ConfigurationProperties(prefix = "crichton.plugin")
@Slf4j
@RequiredArgsConstructor
public class CrichtonPluginProperties {

    private static final String DEFAULT_TRAMPOLINE_PATH = System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "libs" + File.separator + "trampoline";

    private static final String DEFAULT_PLUGIN_PATH = System.getProperty("user.home") + File.separator + ".crichton" + File.separator + "plugin";

    private static final String DEFAULT_INJECTOR_PLUGIN_PATH = DEFAULT_PLUGIN_PATH + File.separator + "injector";

    private static final String DEFAULT_UNIT_TESTER_PLUGIN_PATH = DEFAULT_PLUGIN_PATH + File.separator + "coyote";

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

    @PostConstruct
    public void init() {
        FileUtils.assertFileExists(getInjectorPath(), FileName.INJECTOR_PLUGIN);
        FileUtils.assertFileExists(getUnitTesterPath(), FileName.UNIT_TESTER_PLUGIN);
    }

}
