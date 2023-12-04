package crichton.application.controllers;

import crichton.application.exceptions.CustomException;
import crichton.domain.dtos.ReportDTO;
import crichton.domain.services.ReportService;
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

    @GetMapping("/data")
    @ApiOperation(value = "Client html 레포트 데이터 가공", notes = "Plugins 에서 떨군 csv 레포트를 클라이언트에서 사용할 수 있게 데이터를 가공한다.")
    public ResponseEntity<ReportDTO.DataResponse> getData() throws CustomException {
        ReportDTO.DataResponse data = reportService.getData();
        return ResponseEntity.ok().body(data);
    }


}
