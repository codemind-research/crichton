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
    private String pluginName;
    private LinkedHashMap<String,Object> info;
}
