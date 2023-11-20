package crichton.application.exceptions.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenErrorCode implements ErrorCode{
    INVALID_ACCESS_TOKEN("T001", "The access token provided is invalid"),
    INVALID_REFRESH_TOKEN("T002", "The refresh token provided is invalid");

    private final String code;
    private final String message;
}
