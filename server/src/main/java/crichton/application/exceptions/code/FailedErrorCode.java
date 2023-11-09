package crichton.application.exceptions.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FailedErrorCode implements ErrorCode {
    NOT_EXIST_DIRECTORY("F001", "Not Exist Directory"),
    UNIT_TEST_FAILED("F002", "Unit Test Failed"),
    INJECTION_TEST_FAILED("F003", "Injection Test Failed");

    private final String code;
    private final String message;
}

