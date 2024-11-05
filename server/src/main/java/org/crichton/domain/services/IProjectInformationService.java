package org.crichton.domain.services;

import org.crichton.domain.dtos.project.CreationProjectInformationDto;
import org.crichton.domain.dtos.project.UpdatedProjectInformationDto;
import org.crichton.domain.entities.ProjectInformation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IProjectInformationService<ID> {

    ProjectInformation create(CreationProjectInformationDto creationProjectInformationDto) throws Exception;

    List<ProjectInformation> findAll();

    Optional<ProjectInformation> findById(ID id);

    String getProjectStatus(ID id);

    ProjectInformation update(ID id, UpdatedProjectInformationDto updatedProjectInformationDto);

    void deleteById(ID id) throws IOException;

}
