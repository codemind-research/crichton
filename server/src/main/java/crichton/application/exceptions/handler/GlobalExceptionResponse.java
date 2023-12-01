package crichton.application.exceptions.handler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import crichton.application.exceptions.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GlobalExceptionResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;

    @JsonCreator
    GlobalExceptionResponse(@JsonProperty("code") String code, @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }


    public static ResponseEntity<GlobalExceptionResponse> getResponseFromCustomException(CustomException exception) {
        GlobalExceptionResponse globalExceptionResponse =
                new GlobalExceptionResponse(exception.getErrorCode().getCode(), exception.getErrorCode().getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(globalExceptionResponse);
    }

    public static ResponseEntity<GlobalExceptionResponse> getResponseFromException(Exception exception) {
        GlobalExceptionResponse globalExceptionResponse = new GlobalExceptionResponse("E001", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(globalExceptionResponse);
    }
}
