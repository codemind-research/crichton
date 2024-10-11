package org.crichton.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.crichton.domain.utils.converters.UUIDToStringConverter;
import org.crichton.domain.utils.enums.ProjectStatus;
import org.crichton.domain.utils.enums.TestResult;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class ProjectInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = UUIDToStringConverter.class)
    private UUID uuid;

    private ProjectStatus status;

    @Builder
    public ProjectInformation(UUID uuid, ProjectStatus status) {
        this.uuid = (uuid != null) ? uuid : UUID.randomUUID(); // UUID가 null이면 자동 생성
        this.status = (status != null) ? status : ProjectStatus.None;
    }
}
