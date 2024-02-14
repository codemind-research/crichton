package injector.enumerations;

public enum RunnerStatus {

    DEFECT_INJECTOR_RUNNER("DEFECT_INJECTOR"),
    GOIL_RUNNER("GOIL"),
    MAKE_PYTHON_RUNNER("MAKE_PYTHON"),
    INJECTION_TESTER_RUNNER("INJECTION_TESTER");


    private final String status;

    RunnerStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
