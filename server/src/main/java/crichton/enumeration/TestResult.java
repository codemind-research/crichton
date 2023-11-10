package crichton.enumeration;

public enum TestResult {

    PASS(0),
    SUCCESS(1),
    FAILURE(2);

    public final int value;

    TestResult(int v) {
        this.value = v;
    }

}
