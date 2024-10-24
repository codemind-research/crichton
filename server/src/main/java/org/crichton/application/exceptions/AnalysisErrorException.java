package org.crichton.application.exceptions;

import org.crichton.application.exceptions.code.ErrorCode;

public class AnalysisErrorException extends CustomException {

    public AnalysisErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
