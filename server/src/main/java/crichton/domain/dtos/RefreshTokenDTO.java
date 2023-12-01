package crichton.domain.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {
    @Schema(description = "토큰 만료 시간", required = true)
    private long expireDate;
    @Schema(description = "발행된 토큰 정보 ", required = true)
    private String token;
}
