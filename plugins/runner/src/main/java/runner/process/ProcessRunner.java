package runner.process;

import lombok.NonNull;
import runner.util.CommandBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static runner.Plugin.OUTPUT_PATH;

public abstract class ProcessRunner {

    protected ProcessBuilder processBuilder;

    public ProcessRunner(){
        this.processBuilder = buildProcess();
    }

    public boolean run() {
        try {
            processBuilder.command(buildCommand().getCommand());
            Process process = processBuilder.start();
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            return exitCode == 0;
        }catch (Exception e){
            return false;
        }
    }

    protected abstract CommandBuilder buildCommand();


    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(OUTPUT_PATH.toFile()));
        return processBuilder;
    }

}