package crichton.application.controllers;

import crichton.application.exceptions.CustomException;
import crichton.domian.dtos.ReportDTO;
import crichton.domian.services.ReportService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController("ReportController")
@RequestMapping("/api/v1/crichton/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/data")
    @ApiOperation(value = "csv 레포트 데이터 가공", notes = "Cli 에서 떨군 csv 레포트를 클라이언트에서 사용할 수 있게 데이터를 가공한다.")
    public ResponseEntity<ReportDTO.DataResponse> transformCsvData(@RequestBody ReportDTO.DataRequest request) throws CustomException {
        reportService.transformCsvData(request.getSourcePath());
        return ResponseEntity.ok(null);
    }


}
