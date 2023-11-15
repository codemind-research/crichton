package crichton.domian.services;

import crichton.application.exceptions.CustomException;

public interface ReportService {
    String transformCsvData(String sourcePath) throws CustomException;
}
