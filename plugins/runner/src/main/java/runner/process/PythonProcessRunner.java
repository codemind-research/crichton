package runner.process;

public abstract class PythonProcessRunner extends ProcessRunner {

    private static final String PYTHON = "python";

    @Override
    protected String getProcessName() {
        return PYTHON;
    }
}
