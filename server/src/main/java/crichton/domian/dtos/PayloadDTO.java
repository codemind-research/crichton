package crichton.domian.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayloadDTO {
    @Schema(description = "사용자 고유 ID", required = true)
    private String sub;
    @Schema(description = "토큰 만료 시간 ", required = true)
    private long exp;
}
