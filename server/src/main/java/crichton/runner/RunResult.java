package crichton.runner;

import lombok.Data;

@Data
public class RunResult {
    private boolean isSuccess;
    private final String data;

    RunResult(boolean isSuccess, String data) {
        this.isSuccess = isSuccess;
        this.data = data;
    }

}
