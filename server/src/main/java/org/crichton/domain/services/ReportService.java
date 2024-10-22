package org.crichton.domain.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.crichton.configuration.CrichtonDataStorageProperties;
import org.crichton.domain.dtos.report.ResponseReportDto;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
public class ReportService implements IReportService<UUID> {

    private final CrichtonDataStorageProperties crichtonDataStorageProperties;

    private final IProjectInformationService<UUID> projectInformationService;

    public ReportService(CrichtonDataStorageProperties crichtonDataStorageProperties, IProjectInformationService<UUID> projectInformationService) {
        this.crichtonDataStorageProperties = crichtonDataStorageProperties;
        this.projectInformationService = projectInformationService;
    }


    @Override
    public ResponseReportDto getReportById(UUID id) throws EntityNotFoundException, NoResultException {
        var entity = projectInformationService.findById(id);

        if(!entity.isPresent()) {
            throw new EntityNotFoundException();
        }


        var status = entity.get().getStatus();
        if(status != ProjectStatus.Complete) {
            throw new NoResultException();
        }

        var testResult = entity.get().getTestResult();
        if(testResult != TestResult.Success) {
            throw new NoResultException();
        }

        var uuid = entity.get().getId();

        var workspacePath = crichtonDataStorageProperties.getBasePath() + File.separator + uuid;
        return ResponseReportDto.builder().build();
    }
}
