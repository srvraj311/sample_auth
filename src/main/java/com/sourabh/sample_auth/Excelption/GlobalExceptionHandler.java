package com.sourabh.sample_auth.Excelption;

import com.sourabh.sample_auth.Utils.ApiResponse;
import com.sourabh.sample_auth.Utils.ErrorBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleRuntimeException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorBody error = new ErrorBody(ex.getMessage(), ex.getClass().getSimpleName());

        if (ex.getClass().getSimpleName().equals("BadRequestException")) {
            status = HttpStatus.BAD_REQUEST;
        }
        if (ex.getClass().getSimpleName().equals("UsernameNotFoundException")) {
            status = HttpStatus.NO_CONTENT;
        }
        if (ex.getClass().getSimpleName().equals("AuthenticationException")) {
            status = HttpStatus.UNAUTHORIZED;
        }
        if (ex.getClass().getSimpleName().equals("CredentialNotFoundException")) {
            status = HttpStatus.NO_CONTENT;
        }
        if (ex.getClass().getSimpleName().equals("AccessDeniedException")) {
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<Object>(new ApiResponse( "Error", error, null), status);
    }
}
