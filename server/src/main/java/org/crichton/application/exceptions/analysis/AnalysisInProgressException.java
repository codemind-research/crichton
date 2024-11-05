package org.crichton.application.exceptions.analysis;

import lombok.Getter;
import org.crichton.application.exceptions.CustomException;
import org.crichton.application.exceptions.code.AnalysisErrorCode;
import org.crichton.application.exceptions.code.ErrorCode;

import java.util.UUID;

@Getter
public class AnalysisInProgressException extends CustomException {
    private UUID entityId;
    public AnalysisInProgressException(UUID entityId) {
        super(AnalysisErrorCode.ANALYSIS_IN_PROGRESS);
        this.entityId = entityId;
    }
}
