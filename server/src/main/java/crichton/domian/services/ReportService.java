package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.domian.dtos.ReportDTO;

public interface ReportService {
    ReportDTO.DataResponse transformCsvData(String sourcePath) throws CustomException;
}
