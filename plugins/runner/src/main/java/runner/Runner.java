package runner;


import runner.dto.RunResult;

public interface Runner {
    boolean check();
    RunResult run() throws Exception;
}
