package org.crichton.domain.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.crichton.application.exceptions.analysis.AnalysisErrorException;
import org.crichton.application.exceptions.code.AnalysisErrorCode;
import org.crichton.domain.dtos.report.ResponseReportDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.domain.repositories.IRepository;
import org.crichton.domain.utils.mapper.report.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportService implements IReportService<UUID> {


    private final ReportMapper mapper;
    private final IRepository<ProjectInformation, UUID> repository;

    @Autowired
    public ReportService(ReportMapper mapper, IRepository<ProjectInformation, UUID> repository) {
        this.mapper = mapper;
        this.repository = repository;
    }


    @Override
    public ResponseReportDto getReportByProjectInformationId(UUID id) throws EntityNotFoundException, NoResultException, AnalysisErrorException {
        var entity = repository.findById(id).orElseThrow(EntityNotFoundException::new);


        switch (entity.getStatus()) {
            case None -> throw new AnalysisErrorException(AnalysisErrorCode.ANALYSIS_NOT_STARTED);
            case Running -> throw new AnalysisErrorException(AnalysisErrorCode.ANALYSIS_IN_PROGRESS);
            case Complete -> {
                switch (entity.getTestResult()) {
                    case Fail -> throw new AnalysisErrorException(AnalysisErrorCode.ANALYSIS_FAILED);
                }
            }
            default -> {
                // Do nothing
            }
        }

        return mapper.toResponseReportDto(entity);
    }
}
