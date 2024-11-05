package org.crichton.application.exceptions.analysis;

import org.crichton.application.exceptions.CustomException;
import org.crichton.application.exceptions.code.ErrorCode;

public class AnalysisErrorException extends CustomException {

    public AnalysisErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
