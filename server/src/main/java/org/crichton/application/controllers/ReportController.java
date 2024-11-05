package org.crichton.application.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.crichton.application.exceptions.analysis.AnalysisErrorException;
import org.crichton.application.exceptions.CustomException;
import org.crichton.domain.dtos.report.ResponseReportDto;
import org.crichton.domain.dtos.response.ErrorResponseDto;
import org.crichton.domain.services.IReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Tag(name = "Report Controller", description = "This API is a controller responsible for processing Report Data to be used by the client.")
@CrossOrigin
@RestController("ReportController")
@RequestMapping("/api/v1/crichton/report")
public class ReportController {


    private final IReportService<UUID> reportService;
    private final MessageSource messageSource;

    @Autowired
    public ReportController(IReportService<UUID> reportService, MessageSource messageSource) {
        this.reportService = reportService;
        this.messageSource = messageSource;
    }

    /**
     * 프로젝트 정보 ID를 기반으로 보고서를 조회합니다.
     *
     * @param id 보고서를 조회할 프로젝트 정보의 UUID입니다.
     * @return 조회된 보고서가 성공적으로 반환되거나, 예외 상황에 따라 오류 응답을 반환합니다.
     * @throws CustomException 일반적인 예외가 발생할 경우 발생합니다.
     *
     * @responses
     * - 200: 성공적으로 조회된 보고서 객체 (ResponseReportDto)
     * - 404: 해당 ID의 엔티티를 찾을 수 없음
     * - 400: 분석 오류가 발생한 경우 (ErrorResponseDto)
     * - 500: 서버 내부 오류
     */
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
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }


}
