package crichton.domian.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "Access Token ", required = true)
        private String accessToken;
        @Schema(description = "Refresh Token ", required = true)
        private String refreshToken;
    }
}

