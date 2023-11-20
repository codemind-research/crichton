package crichton.domian.services;

import crichton.application.exceptions.CustomException;
import crichton.application.exceptions.code.FailedErrorCode;
import crichton.domian.dtos.ReportDTO;
import crichton.infrastructure.csv.CsvParser;
import crichton.paths.DirectoryPaths;
import crichton.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;

@Service("ReportService")
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final CsvParser csvParser;
    
    @Override
    public ReportDTO.DataResponse transformCsvData(String sourcePath) throws CustomException{
        File reportPath  = DirectoryPaths.generateUnitReportFilePath(FilenameUtils.getBaseName(sourcePath)).toFile();
        if (!reportPath.exists())
            throw new CustomException(FailedErrorCode.REPORT_NOT_EXIST);
        try {
            StringBuilder csvData = FileUtils.readFile(reportPath);
            return csvParser.parser(csvData.toString());
        }catch (Exception e){
            throw new CustomException(FailedErrorCode.REPORT_DATA_PROCESSING_FAILED);
        }
    }

}
