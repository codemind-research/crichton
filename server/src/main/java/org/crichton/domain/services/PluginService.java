package org.crichton.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.crichton.application.exceptions.analysis.AnalysisErrorException;
import org.crichton.application.exceptions.analysis.AnalysisInProgressException;
import org.crichton.application.exceptions.code.AnalysisErrorCode;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.configuration.CrichtonPluginProperties;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.domain.repositories.PluginProcessorManager;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.models.PluginProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        try {

            log.info("running process check.");
            if(pluginProcessorManager.hasPluginProcessor()) {
                throw new AnalysisInProgressException(entity.getId());
            }

            log.info("entity status check.");
            if(entity.getStatus() == ProjectStatus.Running) {
                throw new AnalysisInProgressException(entity.getId());
            }


            var pluginProcessor = PluginProcessor.builder()
                    .manager(pluginProcessorManager)
                    .targetProject(entity)
                    .baseDirectoryPath(crichtonDataStorageProperties.getBasePath())
                    .defectInjectorPluginPath(crichtonPluginProperties.getInjectorPath())
                    .unitTestPluginPath(crichtonPluginProperties.getUnitTesterPath())
                    .log(log)
                    .build();


            Thread thread = new Thread(pluginProcessor);
            thread.start();

        } catch (Exception e) {
            throw e;
        }
    }

}
