package org.crichton.models.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class StorageDTO {

    @Getter
    @Builder
    public static class StorageResponse {
        @NotNull
        @Schema(description = "Source Code Extraction Path")
        private String unzipPath;
    }

}
