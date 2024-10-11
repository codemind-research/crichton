package org.crichton.models.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {
    @NotNull
    @Schema(description = "Token Expiration Time")
    private long expireDate;
    @NotNull
    @Schema(description = "Issued Token Information")
    private String token;
}
