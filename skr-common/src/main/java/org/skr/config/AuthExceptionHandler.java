package org.skr.config;

import org.skr.common.exception.AuthException;
import org.skr.common.util.Apis;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handleException(AuthException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                Apis.apiResult(ex.getErrors()),
                new HttpHeaders(),
                HttpStatus.FORBIDDEN,
                request);
    }

}
