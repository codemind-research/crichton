package runner.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class RunResult {
    private boolean isSuccess;
    private ProcessedReportDTO data;

    public RunResult(boolean isSuccess, @NonNull ProcessedReportDTO data) {
        this.isSuccess = isSuccess;
        this.data = data;
    }

}