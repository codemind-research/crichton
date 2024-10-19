package org.crichton.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.configuration.CrichtonPluginProperties;
import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.util.FileUtils;
import org.crichton.util.ObjectMapperUtils;
import org.crichton.util.constants.FileName;
import org.crichton.util.constants.PluginSettingKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import runner.PluginRunner;
import runner.paths.PluginPaths;

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
        defectInjectorConfiguration.put(PluginSettingKey.PROJECT_ID, id.toString());

        var projectDirectory = FileUtils.getAbsolutePath(crichtonDataStorageProperties.getBasePath(), entity.getId().toString());
        defectInjectorConfiguration.put(PluginSettingKey.WORKSPACE, projectDirectory + File.separator);
        defectInjectorConfiguration.put(PluginSettingKey.DefectInjector.TEST_SPEC_FILE_NAME, FileName.TEST_SPEC);
        defectInjectorConfiguration.put(PluginSettingKey.DefectInjector.DEFECT_SPEC_FILE_NAME, FileName.DEFECT_SPEC);
        defectInjectorConfiguration.put(PluginSettingKey.DefectInjector.SAFE_SPEC_FILE_NAME, FileName.SAFE_SPEC);
        defectInjectorConfiguration.put(PluginSettingKey.DefectInjector.DEFECT_SIMULATION_OIL_FILE_NAME, FileName.DEFECT_SIMULATION_OIL);
        defectInjectorConfiguration.put(PluginSettingKey.DefectInjector.DEFECT_SIMULATION_EXE_FILE_NAME, FileName.DEFECT_SIMULATION_EXE);

        var propertiesFilePath = PluginPaths.generatePluginPropertiesPath(crichtonPluginProperties.getInjectorPath(), FileName.PLUGIN_PROPERTY_FILE);
        defectInjectorConfiguration.put(PluginSettingKey.DefectInjector.PROPERTIES_PATH, propertiesFilePath.toString());





        defectInjectorPlugin.run(projectDirectory, defectInjectorConfiguration);
    }

    private TestSpecDto getTestSpec(UUID id, Path testSpecFilePath) throws Exception {
        return ObjectMapperUtils.convertJsonFileToObject(testSpecFilePath.toFile(), TestSpecDto.class);
    }



}
