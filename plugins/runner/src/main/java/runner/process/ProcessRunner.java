package runner.process;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import runner.util.CommandBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static runner.Plugin.OUTPUT_PATH;

@Slf4j
public abstract class ProcessRunner {

    protected ProcessBuilder processBuilder;

    public ProcessRunner(){
        this.processBuilder = buildProcess();
    }

    public boolean run() {
        try {
            log.info("Starting Runner: {}", this.getClass().getSimpleName());

            var command = buildCommand().getCommand();
            log.info("executing command: {}", command.stream().collect(Collectors.joining(" ")));

            processBuilder.command(command);
            Process process = processBuilder.start();
            var stdOutHandle = new Thread(new Runnable() {
                @Override
                public void run() {
                    var stream = process.getInputStream();
                    if(stream != null){
                        try(var stdout = new BufferedReader(new InputStreamReader(stream))) {
                            String line;
                            while ((line = stdout.readLine()) != null) {
                                log.trace(line);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {

                        }
                    }

                }
            });




            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            while ((line = errorReader.readLine()) != null) {
                log.trace(line);
            }
            stdOutHandle.start();
            int exitCode = process.waitFor();

            if (process.isAlive()) {
                process.destroyForcibly();
                log.info("Process killed forcefully");
            }
            log.info("Process finished with exit code: {}", exitCode);
            return exitCode == 0;
        }catch (Exception e){
            return false;
        }
    }

    protected abstract CommandBuilder buildCommand();

    protected CommandBuilder buildCommand(List<String> arguments) {
        return buildCommand(null, arguments);
    }

    protected CommandBuilder buildCommand(String ...arguments) {
        return buildCommand(null, arguments);
    }

    protected CommandBuilder buildCommand(String program, List<String> arguments) {
        return buildCommand(program, arguments.toArray(new String[arguments.size()]));
    }

    protected CommandBuilder buildCommand(String program, String ...arguments) {
        var command = new CommandBuilder();

        command.addOption(getProcessName());

        if(program != null){
            command.addOption(program);
        }

        for (String argument : arguments) {
            command.addOption(argument);
        }

        return command;
    }


    protected ProcessBuilder buildProcess(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(OUTPUT_PATH.toFile()));
        return processBuilder;
    }

    protected abstract String getProcessName();

}