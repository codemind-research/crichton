package org.crichton.application.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.crichton.application.exceptions.AnalysisErrorException;
import org.crichton.application.exceptions.CustomException;
import org.crichton.domain.dtos.report.ResponseReportDto;
import org.crichton.domain.dtos.response.ErrorResponseDto;
import org.crichton.domain.services.IReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "Report Controller", description = "This API is a controller responsible for processing Report Data to be used by the client.")
@CrossOrigin
@RestController("ReportController")
@RequestMapping("/api/v1/crichton/report")
@RequiredArgsConstructor
public class ReportController {


    private IReportService<UUID> reportService;

    @Autowired
    public ReportController(IReportService<UUID> reportService) {
        this.reportService = reportService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable("id") UUID id) throws CustomException {
        try {
            ResponseReportDto response = reportService.getReportByProjectInformationId(id);
            return ResponseEntity.ok(response);
        }
        catch (EntityNotFoundException e) {
            log.error("Entity not found", e);
            return ResponseEntity.notFound().build();
        }
        catch (AnalysisErrorException e) {

            log.error("Analysis error occurred", e);

            var errorResponse = new ErrorResponseDto(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
            return ResponseEntity.badRequest()
                    .body(errorResponse);
        }
        catch (CustomException e) {
            log.error(e.getErrorCode().getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            throw e;
        }
    }


}
