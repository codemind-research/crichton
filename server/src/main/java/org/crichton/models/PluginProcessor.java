package org.crichton.models;

import lombok.Builder;
import lombok.Getter;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;
import org.crichton.domain.utils.mapper.InjectorPluginResultMapper;
import org.crichton.util.FileUtils;
import org.crichton.util.ObjectMapperUtils;
import org.crichton.util.constants.DirectoryName;
import org.crichton.util.constants.FileName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.PluginRunner;
import runner.dto.RunResult;
import runner.util.constants.PluginConfigurationKey;

import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class PluginProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PluginProcessor.class);

    @Getter
    private final UUID id = UUID.randomUUID();

    private ProjectInformation targetProject;

    private final String workingDirectoryPath;

    private final String defectInjectorPluginPath;
    private final String defectInjectorPluginPropertiesFilePath;

    private final String unitTestPluginPath;
    private final String unitTestPluginPropertiesFilePath;


    private RunResult injectorPluginRunResult;
    private RunResult unitTestPluginRunResult;


    @Builder
    public PluginProcessor(ProjectInformation targetProject, String baseDirectoryPath, String defectInjectorPluginPath, String unitTestPluginPath) {
        this.targetProject = targetProject;

        this.defectInjectorPluginPath = defectInjectorPluginPath;
        this.defectInjectorPluginPropertiesFilePath = FileUtils.getAbsolutePath(defectInjectorPluginPath, FileName.PLUGIN_PROPERTY_FILE);

        this.unitTestPluginPath = unitTestPluginPath;
        this.unitTestPluginPropertiesFilePath = FileUtils.getAbsolutePath(unitTestPluginPath, FileName.PLUGIN_PROPERTY_FILE);

        this.workingDirectoryPath = FileUtils.getAbsolutePath(baseDirectoryPath, targetProject.getId().toString());
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            log.info("Starting plugin processing...");

            runDefectInjectorPlugin();

            if(Paths.get(this.unitTestPluginPath, FileName.UNIT_TESTER_PLUGIN).toFile().exists()) {
                runUnitTesterPlugin();
            }

            if(!injectorPluginRunResult.getIsSuccess()) {
                throw new RuntimeException("Injector Plugin processing failed");
            }

            if(unitTestPluginRunResult != null && !unitTestPluginRunResult.getIsSuccess()) {
                throw new RuntimeException("Unit test Plugin processing failed");
            }

            targetProject.setInjectorPluginRunResult(InjectorPluginResultMapper.INSTANCE.processedReportDtoToInjectorPluginReport(injectorPluginRunResult.getData()));
            targetProject.setUnitTestPluginRunResult(unitTestPluginRunResult);
            targetProject.updateTestResult(TestResult.Success);

        }
        catch (Exception e) {
            log.error("plugin processing failed", e);

            targetProject.updateTestResult(TestResult.Fail);
            targetProject.updateFailReason(e.getMessage());
        }
        finally {
            targetProject.updateStatus(ProjectStatus.Complete);
            targetProject = null;
        }
    }

    private void runDefectInjectorPlugin() throws Exception {

        Map<String, String> defectInjectorConfiguration = new ConcurrentHashMap<>();

        log.info("setting up defect injector plugin configuration...");

        defectInjectorConfiguration.put(PluginConfigurationKey.WORKSPACE, this.workingDirectoryPath);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DIR_NAME, DirectoryName.DEFECT);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DEFECT_MULTI_PROCESS_MODE, String.valueOf(Boolean.TRUE));
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.TEST_SPEC_FILE_NAME, FileName.TEST_SPEC);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DEFECT_SPEC_FILE_NAME, FileName.DEFECT_SPEC);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.SAFE_SPEC_FILE_NAME, FileName.SAFE_SPEC);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_OIL_FILE_NAME, FileName.DEFECT_SIMULATION_OIL);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_EXE_FILE_NAME, FileName.DEFECT_SIMULATION_EXE);
        defectInjectorConfiguration.put(PluginConfigurationKey.PROPERTIES_PATH, this.defectInjectorPluginPropertiesFilePath);

        try {
            var defectInjectorPlugin = new PluginRunner(this.defectInjectorPluginPath, FileName.INJECTOR_PLUGIN);

            log.info("checking usage of defect injector plugin...");
            if(defectInjectorPlugin.check()) {
                log.info("defect injector plugin check passed.");

                log.info("run defect injector plugin...");
                injectorPluginRunResult = defectInjectorPlugin.run(this.workingDirectoryPath, defectInjectorConfiguration);
            }
            else {
                throw new IllegalStateException("defect injector plugin check failed.");
            }
        }
        catch (Exception e) {
            throw e;
        }



    }

    private void runUnitTesterPlugin() throws Exception {

        log.info("setting up unit-tester plugin configuration...");
        Map<String, String> unitTesterConfiguration = new ConcurrentHashMap<>();
        unitTesterConfiguration.put(PluginConfigurationKey.WORKSPACE, this.workingDirectoryPath);
        unitTesterConfiguration.put(PluginConfigurationKey.UnitTester.DIR_NAME, DirectoryName.UNIT_TEST);
        unitTesterConfiguration.put(PluginConfigurationKey.UnitTester.UNIT_TEST_PROJECT_SETTING_FILE_NAME, FileName.UNIT_TEST_PROJECT_SETTINGS);
        unitTesterConfiguration.put(PluginConfigurationKey.PROPERTIES_PATH, this.unitTestPluginPropertiesFilePath);

        try {
            var unitTestPluginRunner = new PluginRunner(this.unitTestPluginPath, FileName.UNIT_TESTER_PLUGIN);
            unitTestPluginRunner.check();
            log.info("run unit-tester plugin...");
            unitTestPluginRunResult = unitTestPluginRunner.run(this.workingDirectoryPath, unitTesterConfiguration);
        }
        catch (Exception e) {
            throw e;
        }
    }
}
