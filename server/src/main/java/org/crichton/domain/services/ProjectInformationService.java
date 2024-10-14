package org.crichton.domain.services;

import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.project.UpdatedProjectInformationDto;
import org.crichton.domain.entities.ProjectInformation;
import org.crichton.domain.repositories.ProjectInformationRepository;
import org.crichton.domain.utils.mapper.ProjectInformationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectInformationService implements IProjectInformationService {

    @Autowired
    private ProjectInformationRepository repository;

    @Autowired
    private ProjectInformationMapper mapper;

    @Override
    public ProjectInformation create(CreationProjectInformationDto creationProjectInformationDto) {
        var entity = mapper.toEntry(creationProjectInformationDto);
        return repository.save(entity);
    }

    @Override
    public List<ProjectInformation> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ProjectInformation> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public String getProjectStatus(Long id) {
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
    public ProjectInformation update(Long id, UpdatedProjectInformationDto updatedProjectInformationDto) {

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
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
