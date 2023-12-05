package runner;


import lombok.NonNull;
import runner.dto.RunResult;

import java.util.Map;

public interface Runner {
    boolean check();
    RunResult run(@NonNull String targetSource, Map<String, String> pluginSetting) throws Exception;
}
