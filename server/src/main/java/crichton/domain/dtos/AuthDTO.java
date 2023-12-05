package crichton.domain.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenResponse {
        @NotNull
        @Schema(description = "Access Token ")
        private String accessToken;
        @NotNull
        @Schema(description = "Refresh Token ")
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenRequest {
        @NotNull
        @Schema(description = "User Unique ID")
        private String userId;
    }
}

