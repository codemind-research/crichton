package runner.process;

public abstract class DotnetProcessRunner extends ProcessRunner {

    private static final String DOTNET = "dotnet";

    @Override
    protected String getProcessName() {
        return DOTNET;
    }
}
