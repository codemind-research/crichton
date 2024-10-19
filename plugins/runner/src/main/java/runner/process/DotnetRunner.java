package runner.process;

import runner.util.CommandBuilder;

import java.util.List;

public abstract class DotnetRunner extends ProcessRunner {

    private static final String DOTNET = "dotnet";

    protected CommandBuilder buildCommand(String program, List<String> arguments) {
        return buildCommand(program, arguments.toArray(new String[arguments.size()]));
    }

    private CommandBuilder buildCommand(String program, String ...arguments) {
        var command = new CommandBuilder();

        command.addOption(DOTNET);
        command.addOption(program);

        for (String argument : arguments) {
            command.addOption(argument);
        }

        return command;
    }
}
