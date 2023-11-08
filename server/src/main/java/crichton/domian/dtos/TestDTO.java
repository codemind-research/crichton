package crichton.domian.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class TestDTO {

    @Getter
    @Builder
    public static class TestResponse {
        @Schema(description = "테스트 응답 메세지", required = true)
        private String message;
    }
}
