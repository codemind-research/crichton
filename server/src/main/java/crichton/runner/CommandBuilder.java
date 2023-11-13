package crichton.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CommandBuilder {

    private final List<String> command;

    public CommandBuilder() {
        this.command = new ArrayList<>();
    }

    public List<String> getCommand() {
        return command;
    }

    public CommandBuilder addOption(String option) {
        this.command.add(option);
        return this;
    }

    public CommandBuilder addOption(String option, int value) {
        this.command.add(option);
        this.command.add(String.valueOf(value));
        return this;
    }


    public CommandBuilder addOption(String option, String value) {
        this.command.add(option);
        this.command.add(value);
        return this;
    }

    public CommandBuilder checkAndAddOption(String option, int value, Supplier<Boolean> checker) {
        if (checker.get()) {
            this.addOption(option, value);
        }
        return this;
    }

    public CommandBuilder checkAndAddOption(String option, String value, Supplier<Boolean> checker) {
        if (checker.get()) {
            this.addOption(option, value);
        }
        return this;
    }


}
