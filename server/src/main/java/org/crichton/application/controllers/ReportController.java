package org.crichton.application.controllers;

import org.crichton.application.exceptions.CustomException;
import org.crichton.domain.dtos.ReportDTO;
import org.crichton.domain.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report Controller", description = "This API is a controller responsible for processing Report Data to be used by the client.")
@CrossOrigin
@RestController("ReportController")
@RequestMapping("/api/v1/crichton/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/data")
    @Operation(summary = "Process Client HTML Report Data", description = "Processes CSV reports sent by plugins to make the data available for the client." +
            " This API is responsible for transforming the provided CSV reports into a format usable by the client for generating HTML reports.")
    public ResponseEntity<ReportDTO.DataResponse> getData() throws CustomException {
        ReportDTO.DataResponse response = reportService.getData();
        return ResponseEntity.ok().body(response);
    }


}
