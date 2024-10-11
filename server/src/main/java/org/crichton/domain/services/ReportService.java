package org.crichton.domain.services;

import org.crichton.application.exceptions.CustomException;
import org.crichton.models.dtos.ReportDTO;

public interface ReportService {
    ReportDTO.DataResponse getData() throws CustomException;
}
