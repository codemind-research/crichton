package crichton.domain.dtos;

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
public class PayloadDTO {
    @NotNull
    @Schema(description = "User Unique ID")
    private String sub;
    @NotNull
    @Schema(description = "Token Expiration Time ")
    private long exp;
}
