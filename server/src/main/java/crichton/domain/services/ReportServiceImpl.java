package crichton.domain.services;

import crichton.application.exceptions.CustomException;
import crichton.domain.dtos.ReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service("ReportService")
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    
    @Override
    public ReportDTO.DataResponse getData() throws CustomException{
        return new ReportDTO.DataResponse();
    }

}
