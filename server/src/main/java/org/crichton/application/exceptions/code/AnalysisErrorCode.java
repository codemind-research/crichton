package org.crichton.application.exceptions.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AnalysisErrorCode implements ErrorCode {

    ANALYSIS_IN_PROGRESS("A001", "Analysis In Progress"),
    ANALYSIS_NOT_STARTED("A002", "Analysis Not Started"),
    ANALYSIS_FAILED("A003", "Analysis Failed"),
    REPORT_REQUESTED_BEFORE_COMPLETION("A004", "Report Requested Before Analysis Completion");

    private final String code;
    private final String message;
}

