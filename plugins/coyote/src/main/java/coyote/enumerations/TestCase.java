package coyote.enumerations;

public enum TestCase {

    NULL_ACCESS("Null_Access"),
    SEGFAULTS("SegFaults"),
    DIV_ZERO("Div_Zero"),
    ARRAY_OUT_OF_BOUND("ArrayOutOfBound"),
    ASSERTS("Asserts"),
    TIMEOUTS("TimeOuts");

    private final String type;

    TestCase(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
