package org.crichton.domain.repositories;

import org.crichton.domain.entities.ProjectInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectInformationRepository extends JpaRepository<ProjectInformation, Long> {
}
