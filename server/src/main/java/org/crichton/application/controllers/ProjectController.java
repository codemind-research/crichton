package org.crichton.application.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.response.CreatedResponseDto;
import org.crichton.domain.dtos.response.ProjectStatusResponseDto;
import org.crichton.domain.services.IProjectInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Project", description = "This API handles analytics requests, viewing analytics status, and removing analytics for analytics targets requested by users.")
@CrossOrigin
@RestController("ProjectController")
@RequestMapping("/api/v1/crichton/project")
@RequiredArgsConstructor
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private IProjectInformationService<UUID> projectInformationService;

    @Autowired
    public ProjectController(IProjectInformationService<UUID> projectInformationService) {
        this.projectInformationService = projectInformationService;
    }

    @Operation(summary = "분석하기", description = "분석 대상을 프로젝트로 생성하여 분석을 진행합니다.")
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
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Operation(summary = "프로젝트 상태 조회", description = "프로젝트의 상태를 조회 합니다.")
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
    public ResponseEntity<String> deleteProject(
            @Parameter(description = "분석 요청시 전달 받은 ID", required = true) @PathVariable UUID id) {
        try {
            projectInformationService.deleteById(id);
            return ResponseEntity.ok().build();
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
