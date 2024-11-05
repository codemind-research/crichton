package org.crichton.application.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.crichton.application.exceptions.analysis.AnalysisErrorException;
import org.crichton.application.exceptions.analysis.AnalysisInProgressException;
import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.response.CreatedResponseDto;
import org.crichton.domain.dtos.response.ProjectStatusResponseDto;
import org.crichton.domain.services.IProjectInformationService;
import org.crichton.domain.services.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Project Controller", description = "This API handles analytics requests, viewing analytics status, and removing analytics for analytics targets requested by users.")
@CrossOrigin
@RestController("ProjectController")
@RequestMapping("/api/v1/crichton/project")
@RequiredArgsConstructor
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private IProjectInformationService<UUID> projectInformationService;
    private final PluginService pluginService;

    @Autowired
    public ProjectController(IProjectInformationService<UUID> projectInformationService, PluginService pluginService) {
        this.projectInformationService = projectInformationService;
        this.pluginService = pluginService;
    }

    @PostMapping(value = "/run", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProject(@Valid @ModelAttribute CreationProjectInformationDto creationProjectInformationDto) {
        try {
            var entity =  projectInformationService.create(creationProjectInformationDto);
            if(entity != null) {

                return ResponseEntity.ok(new CreatedResponseDto(entity.getId()));
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        catch (AnalysisInProgressException e) {
            logger.error(e.getErrorCode().getMessage(), e);
            Map<String, String> body = new HashMap<>();
            body.put("id", e.getEntityId().toString());
            body.put("message", "이미 분석 중인 프로젝트가 있습니다. 다음에 다시하세요.");
            return ResponseEntity.badRequest().body(body);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/run/retry/{id}")
    public ResponseEntity<?> retryAnalyis(@Parameter(description = "분석 요청시 전달 받은 ID", required = true) @PathVariable UUID id) {
        try {
            var entity =  projectInformationService.findById(id).orElseThrow(EntityNotFoundException::new);
            pluginService.runPlugin(entity);
            return ResponseEntity.ok(new CreatedResponseDto(entity.getId()));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (AnalysisInProgressException e) {
            logger.error(e.getErrorCode().getMessage(), e);
            Map<String, String> body = new HashMap<>();
            body.put("id", e.getEntityId().toString());
            body.put("message", "이미 분석 중인 프로젝트가 있습니다. 다음에 다시하세요.");
            return ResponseEntity.badRequest().body(body);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/status/{id}")
    public ResponseEntity<?> getProjectStatus(
            @Parameter(description = "분석 요청시 전달 받은 ID", required = true) @PathVariable UUID id) {
        try {
            var projectStatus = projectInformationService.getProjectStatus(id);
            var response = ProjectStatusResponseDto.builder()
                    .id(id)
                    .status(projectStatus)
                    .build();
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> deleteProject(
            @Parameter(description = "분석 요청시 전달 받은 ID", required = true) @PathVariable UUID id) {
        try {
            projectInformationService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        catch (EntityNotFoundException e) {
            logger.error("Project with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
