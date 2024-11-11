package runner;


import lombok.NonNull;
import runner.dto.RunResult;

import java.nio.file.NoSuchFileException;
import java.util.Map;

public interface Runner {
    boolean check() throws NoSuchFileException;
    RunResult run(@NonNull String targetSource, Map<String, String> pluginSetting) throws Exception;
}
