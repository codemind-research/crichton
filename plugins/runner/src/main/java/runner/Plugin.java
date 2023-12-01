package runner;

import lombok.NonNull;
import runner.dto.ProcessedReportDTO;
import runner.dto.RunResult;
import runner.paths.PluginPaths;
import runner.util.CommandBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface Plugin {

    Path OUTPUT_PATH = PluginPaths.CRICHTON_LOG_PATH;

    //Plugin 실행하기전 check
    boolean check();

    //Plugin 설정 초기화 함수
    void initialize(@NonNull String targetSource, Map<String,String> pluginSetting) throws IOException;

    //Plugin 실행함수
    boolean execute() throws IOException;

    //Plugin 관련 Command 생성
    default CommandBuilder buildCommand() {
        return new CommandBuilder();
    }

    //Plugin 관련 Process 생성
    default ProcessBuilder buildProcess(@NonNull List<String> command){
        return new ProcessBuilder();
    }

    //Plugin 관련 레포트 데이터 가공 생성
    ProcessedReportDTO transformReportData() throws Exception;


}
