package runner.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class RunResult {
    private Boolean isSuccess;
    private ProcessedReportDTO data;

    public RunResult(Boolean isSuccess, @NonNull ProcessedReportDTO data) {
        this.isSuccess = isSuccess;
        this.data = data;
    }

}