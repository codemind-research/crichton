package org.crichton.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.configuration.CrichtonPluginProperties;
import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.util.FileUtils;
import org.crichton.util.ObjectMapperUtils;
import org.crichton.util.constants.DirectoryName;
import org.crichton.util.constants.FileName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import runner.PluginRunner;
import runner.paths.PluginPaths;
import runner.util.constants.PluginConfigurationKey;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PluginService {

    private static final String INJECTOR_PLUGIN = "injector";
    private static final String COYOTE_PLUGIN = "coyote";

    private final CrichtonDataStorageProperties crichtonDataStorageProperties;
    private final CrichtonPluginProperties crichtonPluginProperties;

    @Autowired
    public PluginService(CrichtonDataStorageProperties crichtonDataStorageProperties, CrichtonPluginProperties crichtonPluginProperties) {
        this.crichtonDataStorageProperties = crichtonDataStorageProperties;
        this.crichtonPluginProperties = crichtonPluginProperties;
    }


    public void run(ProjectInformation entity) throws Exception {
        try {

            runDefectInjector(entity);
            runInjectionTester(entity);


        }
        catch (Exception e) {
            throw e;
        }
    }

    private void runDefectInjector(ProjectInformation entity) throws Exception {
        var defectInjectorPlugin = new PluginRunner(crichtonPluginProperties.getInjectorPath(), FileName.INJECTOR_PLUGIN);
        defectInjectorPlugin.check();


        Map<String, String> defectInjectorConfiguration = new ConcurrentHashMap<>();

        var id = entity.getId();
        defectInjectorConfiguration.put(PluginConfigurationKey.PROJECT_ID, id.toString());

        var projectDirectory = FileUtils.getAbsolutePath(crichtonDataStorageProperties.getBasePath(), entity.getId().toString());
        defectInjectorConfiguration.put(PluginConfigurationKey.WORKSPACE, projectDirectory);

        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DIR_NAME, DirectoryName.DEFECT);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.TEST_SPEC_FILE_NAME, FileName.TEST_SPEC);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DEFECT_SPEC_FILE_NAME, FileName.DEFECT_SPEC);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.SAFE_SPEC_FILE_NAME, FileName.SAFE_SPEC);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_OIL_FILE_NAME, FileName.DEFECT_SIMULATION_OIL);
        defectInjectorConfiguration.put(PluginConfigurationKey.DefectInjector.DEFECT_SIMULATION_EXE_FILE_NAME, FileName.DEFECT_SIMULATION_EXE);

        var propertiesFilePath = PluginPaths.generatePluginPropertiesPath(crichtonPluginProperties.getInjectorPath(), FileName.PLUGIN_PROPERTY_FILE);
        defectInjectorConfiguration.put(PluginConfigurationKey.PROPERTIES_PATH, propertiesFilePath.toString());


        defectInjectorPlugin.run(projectDirectory, defectInjectorConfiguration);
    }

    private void runInjectionTester(ProjectInformation entity) throws Exception {
        var defectInjectorPlugin = new PluginRunner(crichtonPluginProperties.getUnitTesterPath(), FileName.UNIT_TESTER_PLUGIN);
        defectInjectorPlugin.check();


        Map<String, String> unitTesterConfiguration = new ConcurrentHashMap<>();

        var id = entity.getId();
        unitTesterConfiguration.put(PluginConfigurationKey.PROJECT_ID, id.toString());

        var projectDirectory = FileUtils.getAbsolutePath(crichtonDataStorageProperties.getBasePath(), entity.getId().toString());
        unitTesterConfiguration.put(PluginConfigurationKey.WORKSPACE, projectDirectory);

        unitTesterConfiguration.put(PluginConfigurationKey.UnitTester.DIR_NAME, DirectoryName.UNIT_TEST);
        unitTesterConfiguration.put(PluginConfigurationKey.UnitTester.UNIT_TEST_PROJECT_SETTING_FILE_NAME, FileName.UNIT_TEST_SPEC);

        var propertiesFilePath = PluginPaths.generatePluginPropertiesPath(crichtonPluginProperties.getInjectorPath(), FileName.PLUGIN_PROPERTY_FILE);
        unitTesterConfiguration.put(PluginConfigurationKey.PROPERTIES_PATH, propertiesFilePath.toString());

        defectInjectorPlugin.run(projectDirectory, unitTesterConfiguration);
    }

    private TestSpecDto getTestSpec(UUID id, Path testSpecFilePath) throws Exception {
        return ObjectMapperUtils.convertJsonFileToObject(testSpecFilePath.toFile(), TestSpecDto.class);
    }



}
