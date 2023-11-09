package crichton.executors;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;

public class ProcessRunner {


    public static class RunResult {
        private final int exitCode;
        private final String output;

        private RunResult(Integer code, String outPut) {
            this.exitCode = code;
            this.output = outPut;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getOutput() {
            return output;
        }

    }

    public static RunResult run(String dir, CommandBuilder command) throws Exception {
        ProcessExecutor processExecutor = new ProcessExecutor()
                    .command(command.getCommand())
                    .directory(new File(dir))
                    .readOutput(true);

        ProcessResult result = processExecutor.execute();
        return new RunResult(result.getExitValue(), result.outputUTF8());
    }

}
