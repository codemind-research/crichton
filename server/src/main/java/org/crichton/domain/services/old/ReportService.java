package org.crichton.domain.services.old;

import org.crichton.application.exceptions.CustomException;
import org.crichton.domain.dtos.ReportDTO;

public interface ReportService {
    ReportDTO.DataResponse getData() throws CustomException;
}
