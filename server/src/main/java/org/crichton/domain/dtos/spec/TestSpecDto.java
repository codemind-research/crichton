package org.crichton.domain.dtos.spec;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestSpecDto {

    @Builder.Default
    private List<TaskDto> tasks = new ArrayList<>();

    @JsonProperty("stop")
    private long stopDuration;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskDto {

        private String name;

        @JsonProperty("start")
        private long startTime;

        @JsonProperty("cycle")
        private long cycleDuration;

        private long priority;

        private String file;
    }

}
