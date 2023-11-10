package crichton.domian.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class StorageDTO {

    @Getter
    @Builder
    public static class StorageResponse {
        @Schema(description = "소스코드 압축해제 경로", required = true)
        private String unzipPath;
    }

}
