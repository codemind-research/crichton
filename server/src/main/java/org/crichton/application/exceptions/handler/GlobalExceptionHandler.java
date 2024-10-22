package org.crichton.application.exceptions.handler;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.crichton.application.exceptions.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected ResponseEntity<GlobalExceptionResponse> handleCustomException(CustomException e) {
        e.printStackTrace();
        return GlobalExceptionResponse.getResponseFromCustomException(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected ResponseEntity<GlobalExceptionResponse> handleException(Exception e) {
        e.printStackTrace();
        return GlobalExceptionResponse.getResponseFromException(e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<GlobalExceptionResponse> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {

        constraintViolationException.getConstraintViolations().stream().peek(o -> log.error(o.getMessage()));

        return new ResponseEntity<>(GlobalExceptionResponse.builder()
                                            .message("유효하지 않은 입력값입니다.")
                                            .code(HttpStatus.BAD_REQUEST.toString()).build() // bad request
                , HttpStatus.BAD_REQUEST);
    }



}
