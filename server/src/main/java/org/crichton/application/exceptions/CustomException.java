package org.crichton.application.exceptions;

import org.crichton.application.exceptions.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends Exception {
    private final ErrorCode errorCode;
}