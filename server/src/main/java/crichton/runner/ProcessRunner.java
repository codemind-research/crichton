package crichton.runner;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.util.Optional;

public abstract class ProcessRunner implements Runner {

    protected ProcessExecutor processExecutor;

    public ProcessRunner(){
        this.processExecutor = createProcessExecutor();
    }

    @Override
    public Optional<RunResult> run() throws Exception {
        processExecutor.command(builder().getCommand());
        ProcessResult result = processExecutor.execute();
        return result.getExitValue() == 0 ?
                Optional.of(new RunResult(result.getExitValue() == 0, result.outputUTF8()))
                : Optional.empty();
    }

    protected abstract ProcessExecutor createProcessExecutor();

}
