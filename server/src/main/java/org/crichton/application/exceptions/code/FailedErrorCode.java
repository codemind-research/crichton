package crichton.application.exceptions.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FailedErrorCode implements ErrorCode {
    NOT_EXIST_TARGET_DIRECTORY("F001", "Not Exist Target Directory"),
    UPLOAD_FAILED("F002", "Upload Failed"),
    REPORT_DATA_PROCESSING_FAILED("F003", "Report Data Processing Failed"),
    NOT_EXIST_PLUGINS("F004", "Not Exist Plugins");

    private final String code;
    private final String message;
}

