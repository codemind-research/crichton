package runner;


import runner.dto.RunResult;

public interface Runner {
    RunResult run() throws Exception;
}
