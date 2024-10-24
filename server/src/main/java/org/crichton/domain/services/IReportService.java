package org.crichton.domain.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.crichton.application.exceptions.AnalysisErrorException;
import org.crichton.domain.dtos.report.ResponseReportDto;

public interface IReportService<ID> {
    public ResponseReportDto getReportByProjectInformationId(ID id) throws EntityNotFoundException, NoResultException, AnalysisErrorException;
}
