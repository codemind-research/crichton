package org.crichton.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.configuration.CrichtonPluginProperties;
import org.crichton.domain.dtos.spec.TestSpecDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.domain.repositories.PluginProcessorManager;
import org.crichton.models.PluginProcessor;
import org.crichton.util.FileUtils;
import org.crichton.util.ObjectMapperUtils;
import org.crichton.util.constants.DirectoryName;
import org.crichton.util.constants.FileName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import runner.PluginRunner;
import runner.paths.PluginPaths;
import runner.util.constants.PluginConfigurationKey;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PluginService {

    private final PluginProcessorManager pluginProcessorManager;
    private final CrichtonDataStorageProperties crichtonDataStorageProperties;
    private final CrichtonPluginProperties crichtonPluginProperties;

    @Autowired
    public PluginService(PluginProcessorManager pluginProcessorManager, CrichtonDataStorageProperties crichtonDataStorageProperties, CrichtonPluginProperties crichtonPluginProperties) {
        this.pluginProcessorManager = pluginProcessorManager;
        this.crichtonDataStorageProperties = crichtonDataStorageProperties;
        this.crichtonPluginProperties = crichtonPluginProperties;
    }


    public void runPlugin(ProjectInformation entity) throws Exception {


        var pluginProcessor = PluginProcessor.builder()
                .targetProject(entity)
                .baseDirectoryPath(crichtonDataStorageProperties.getBasePath())
                .defectInjectorPluginPath(crichtonPluginProperties.getInjectorPath())
                .unitTestPluginPath(crichtonPluginProperties.getUnitTesterPath())
                .log(log)
                .build();
        try {

            entity.updatePluginProcessorId(pluginProcessor.getId());
            pluginProcessorManager.save(pluginProcessor);

            Thread thread = new Thread(pluginProcessor);
            thread.start();

        } catch (Exception e) {
            throw e;
        } finally {
            pluginProcessorManager.deleteById(pluginProcessor.getId());
        }
    }

}
