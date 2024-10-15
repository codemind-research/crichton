package org.crichton.domain.services;

import lombok.extern.slf4j.Slf4j;
import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.project.UpdatedProjectInformationDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.domain.repositories.IRepository;
import org.crichton.domain.utils.mapper.ProjectInformationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ProjectInformationService implements IProjectInformationService<UUID> {

    private final IRepository<ProjectInformation, UUID> repository;

    private final ProjectInformationMapper mapper;

    @Autowired
    public ProjectInformationService(IRepository<ProjectInformation, UUID> repository, ProjectInformationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProjectInformation create(CreationProjectInformationDto creationProjectInformationDto) {

        log.info("creating project information");

        var entity = mapper.toEntry(creationProjectInformationDto);
        return repository.save(entity);
    }

    @Override
    public List<ProjectInformation> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ProjectInformation> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public String getProjectStatus(UUID id) {
        var entity = repository.findById(id).orElse(null);
        if(entity != null) {
            return switch (entity.getStatus()) {
                case None, Running -> "testing";
                case Complete -> switch(entity.getTestResult()) {
                    case None -> "testing";
                    case Success -> "pass";
                    case Fail -> "fail";
                };
            };
        }
        else {
            return "none";
        }
    }

    @Override
    @Transactional
    public ProjectInformation update(UUID id, UpdatedProjectInformationDto updatedProjectInformationDto) {

        ProjectInformation existingProject = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project with id " + id + " not found."));

        if(updatedProjectInformationDto.getStatus() != null) {
            existingProject.updateStatus(updatedProjectInformationDto.getStatus());
        }

        if(updatedProjectInformationDto.getTestResult() != null) {
            existingProject.updateTestResult(updatedProjectInformationDto.getTestResult());
        }

        if(updatedProjectInformationDto.getFailReason() != null) {
            existingProject.updateFailReason(updatedProjectInformationDto.getFailReason());
        }

        return existingProject;
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
