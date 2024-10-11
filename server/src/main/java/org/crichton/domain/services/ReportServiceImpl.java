package org.crichton.domain.services;

import org.crichton.Infrastructure.store.TestResultMemoryStorage;
import org.crichton.application.exceptions.CustomException;
import org.crichton.domain.dtos.ReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import runner.dto.ProcessedReportDTO;

import java.util.LinkedList;


@Service("ReportService")
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final TestResultMemoryStorage storage;
    
    @Override
    public ReportDTO.DataResponse getData() throws CustomException {
        LinkedList<ProcessedReportDTO> data = storage.getAllTestResults();
        return new ReportDTO.DataResponse(data);
    }

}
