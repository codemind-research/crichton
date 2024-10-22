package org.crichton.domain.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.crichton.domain.dtos.report.ResponseReportDto;

public interface IReportService<ID> {
    public ResponseReportDto getReportById(ID id) throws EntityNotFoundException, NoResultException;
}
