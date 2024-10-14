package org.crichton.domain.services;

import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.project.UpdatedProjectInformationDto;
import org.crichton.domain.entities.ProjectInformation;

import java.util.List;
import java.util.Optional;

public interface IProjectInformationService {

    ProjectInformation create(CreationProjectInformationDto creationProjectInformationDto);

    List<ProjectInformation> findAll();

    Optional<ProjectInformation> findById(Long id);

    ProjectInformation update(Long id, UpdatedProjectInformationDto updatedProjectInformationDto);

    void deleteById(Long id);

}
