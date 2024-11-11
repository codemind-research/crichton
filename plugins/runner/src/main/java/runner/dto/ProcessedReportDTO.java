package runner.dto;

import lombok.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedReportDTO {

    @Builder.Default
    private String pluginName = "unknown plugin";

    @Builder.Default
    private LinkedHashMap<String,Object> info = new LinkedHashMap<>();
}
