package org.crichton.application.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.services.IProjectInformationService;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report Controller", description = "This API is a controller responsible for processing Report Data to be used by the client.")
@CrossOrigin
@RestController("ProjectController")
@RequestMapping("/api/v1/crichton/project")
@RequiredArgsConstructor
public class ProjectController {

    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private IProjectInformationService projectInformationService;

    @Autowired
    public ProjectController(IProjectInformationService projectInformationService) {
        this.projectInformationService = projectInformationService;
    }

    @PostMapping(value = "/run", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createProject(CreationProjectInformationDto creationProjectInformationDto) {
        try {
            var entity =  projectInformationService.create(creationProjectInformationDto);
            if(entity != null) {
                return ResponseEntity.ok(entity.getId());
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

    @GetMapping(value = "/status/{id}")
    public ResponseEntity<String> getProjectStatus(@PathVariable Long id) {
        try {
            var projectStatus = projectInformationService.getProjectStatus(id);
            return ResponseEntity.ok(projectStatus);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        try {
            projectInformationService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}