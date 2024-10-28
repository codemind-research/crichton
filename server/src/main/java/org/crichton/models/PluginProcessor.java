package org.crichton.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.domain.repositories.PluginProcessorManager;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;
import org.crichton.domain.utils.mapper.report.InjectorPluginResultMapper;
import org.crichton.domain.utils.mapper.report.UnitTesterPluginResultMapper;
import org.crichton.util.FileUtils;
import org.crichton.util.ObjectMapperUtils;
import org.crichton.util.constants.DirectoryName;
import org.crichton.util.constants.FileName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runner.PluginRunner;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.util.constants.PluginConfigurationKey;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class PluginProcessor implements Runnable {

    private final Logger log;

    private final PluginProcessorManager manager;

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
    public PluginProcessor(@NonNull PluginProcessorManager manager, @NonNull ProjectInformation targetProject, @NonNull String baseDirectoryPath, @NonNull String defectInjectorPluginPath, @NonNull String unitTestPluginPath, Logger log) {

        this.manager = manager;
        manager.save(this);

        this.targetProject = targetProject;
        targetProject.updatePluginProcessorId(id);

        this.workingDirectoryPath = FileUtils.getAbsolutePath(baseDirectoryPath, targetProject.getId().toString());

        this.defectInjectorPluginPath = defectInjectorPluginPath;
        this.defectInjectorPluginPropertiesFilePath = FileUtils.getAbsolutePath(defectInjectorPluginPath, FileName.PLUGIN_PROPERTY_FILE);

        this.unitTestPluginPath = unitTestPluginPath;
        this.unitTestPluginPropertiesFilePath = FileUtils.getAbsolutePath(unitTestPluginPath, FileName.PLUGIN_PROPERTY_FILE);


        if(log == null) {
            this.log = LoggerFactory.getLogger(PluginProcessor.class);
        }
        else {
            this.log = log;
        }


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

            if(injectorPluginRunResult != null && !injectorPluginRunResult.getIsSuccess()) {
                throw new RuntimeException("Injector Plugin processing failed");
            }
            else if(injectorPluginRunResult != null && injectorPluginRunResult.getIsSuccess()) {
                log.debug("Save injector plugin result report.");
                targetProject.setInjectorPluginReport(InjectorPluginResultMapper.INSTANCE.toInjectorPluginReport(injectorPluginRunResult.getData()));
            }
            else {
                log.info("Skip injector plugin processing.");
            }

            if(unitTestPluginRunResult != null && !unitTestPluginRunResult.getIsSuccess()) {
                throw new RuntimeException("Injector Plugin processing failed");
            }
            else if(unitTestPluginRunResult != null && unitTestPluginRunResult.getIsSuccess()) {
                log.info("Save unit tester plugin result report.");
                targetProject.setUnitTestPluginReport(UnitTesterPluginResultMapper.INSTANCE.toUnitTestPluginReport(unitTestPluginRunResult.getData()));
            }
            else {
                log.info("Skip unit tester plugin processing.");
            }

            targetProject.updateTestResult(TestResult.Success);

        }
        catch (Exception e) {
            log.error("plugin processing failed", e);

            targetProject.updateTestResult(TestResult.Fail);
            targetProject.updateFailReason(e.getMessage());
        }
        finally {

            log.trace("Disposing plugin resource...");
            targetProject.updateStatus(ProjectStatus.Complete);
            targetProject.updatePluginProcessorId(null);
            targetProject = null;

            log.info("Manager [{}] is deleting processor with ID: {}", manager.getClass().getSimpleName(), id);
            manager.deleteById(id);

            log.info("Finished plugin processing.");
        }
    }

    private void runDefectInjectorPlugin() throws Exception {

        Map<String, String> defectInjectorConfiguration = new ConcurrentHashMap<>();

        log.info("setting up defect injector plugin configuration...");

        defectInjectorConfiguration.put(PluginConfigurationKey.WORKSPACE, this.workingDirectoryPath);
        defectInjectorConfiguration.put(PluginConfigurationKey.SOURCE_DIRECTORY_NAME, DirectoryName.SOURCE);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DIRECTORY_NAME, DirectoryName.INJECT_TEST);
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
                String sourceDirectory = FileUtils.getAbsolutePath(this.workingDirectoryPath, DirectoryName.SOURCE);
                injectorPluginRunResult = defectInjectorPlugin.run(sourceDirectory, defectInjectorConfiguration);
            }
            else {
                throw new IllegalStateException("defect injector plugin check failed.");
            }
        }
        catch (IllegalStateException e) {
            log.warn("defect injector plugin check failed", e);
        }
        catch (Exception e) {
            log.warn("defect injector plugin failed", e);
        }
        finally {
            var sourceDirectory = Paths.get(this.workingDirectoryPath).resolve(DirectoryName.SOURCE);
            var deletingTargets = List.of(
                    sourceDirectory.resolve(DirectoryName.DEFECT_SIMULATION).toFile(),
                    sourceDirectory.resolve(FileName.DEFECT_SIMULATION_OIL).toFile(),
                    sourceDirectory.resolve(FileName.DEFECT_SIMULATION_EXE).toFile(),
                    sourceDirectory.resolve(FileName.DEFECT_SIMULATION_SOURCE).toFile()
            ).stream().filter(File::exists).collect(Collectors.toUnmodifiableList());

            for (var target : deletingTargets) {
                if(target.isDirectory()) {
                    Files.walkFileTree(target.toPath(), new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
                else {
                    Files.delete(target.toPath());
                }
            }

            var defectSimulationDirectory = sourceDirectory.resolve(DirectoryName.DEFECT_SIMULATION);
            var defectSimulationOliFile = sourceDirectory.resolve(FileName.DEFECT_SIMULATION_OIL);
            var defectSimulationExeFile = sourceDirectory.resolve(FileName.DEFECT_SIMULATION_EXE);
            var defectSimulationSourceFile = sourceDirectory.resolve(FileName.DEFECT_SIMULATION_SOURCE);
        }



    }

    private void runUnitTesterPlugin() throws Exception {

        log.info("setting up unit-tester plugin configuration...");
        Map<String, String> unitTesterConfiguration = new ConcurrentHashMap<>();
        unitTesterConfiguration.put(PluginConfigurationKey.WORKSPACE, this.workingDirectoryPath);
        unitTesterConfiguration.put(PluginConfigurationKey.SOURCE_DIRECTORY_NAME, DirectoryName.SOURCE);
        unitTesterConfiguration.put(PluginConfigurationKey.UnitTester.DIRECTORY_NAME, DirectoryName.UNIT_TEST);
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
