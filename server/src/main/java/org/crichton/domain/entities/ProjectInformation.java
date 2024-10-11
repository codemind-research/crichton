package org.crichton.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.crichton.domain.utils.converters.UUIDToStringConverter;

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

    @Builder
    public ProjectInformation(UUID uuid) {
        this.uuid = (uuid != null) ? uuid : UUID.randomUUID(); // UUID가 null이면 자동 생성
    }
}
