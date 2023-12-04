package crichton.application.exceptions.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FailedErrorCode implements ErrorCode {
    NOT_EXIST_TARGET_DIRECTORY("F001", "Not Exist Target Directory"),
    UPLOAD_FAILED("F002", "Upload Failed"),
    LOG_READ_FAILED("F003", "Log Read Failed"),
    REPORT_NOT_EXIST("F004", "Report Not Exist"),
    REPORT_DATA_PROCESSING_FAILED("F005", "Report Data Processing Failed"),
    NOT_EXIST_PLUGINS("F006", "Not Exist Plugins");

    private final String code;
    private final String message;
}

