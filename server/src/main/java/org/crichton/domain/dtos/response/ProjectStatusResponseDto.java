package org.crichton.domain.dtos.response;

import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

@Builder
public record ProjectStatusResponseDto(@NonNull UUID id, @NonNull String status) {
}
