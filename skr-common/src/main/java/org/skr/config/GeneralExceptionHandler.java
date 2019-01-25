package org.skr.config;

import org.skr.common.exception.Errors;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.BizException;
import org.skr.common.exception.UnvarnishedFeignException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        Object standerBody = Optional.ofNullable(body)
                .orElse(Errors.INTERNAL_SERVER_ERROR.setMsg(ex.getMessage()));
        return super.handleExceptionInternal(ex, standerBody, headers, status, request);
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Object> handleException(BizException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                ex.getErrors(),
                new HttpHeaders(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handleException(AuthException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                ex.getErrors(),
                new HttpHeaders(),
                HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler(UnvarnishedFeignException.class)
    public ResponseEntity<Object> handleException(UnvarnishedFeignException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                ex.getErrors(),
                new HttpHeaders(),
                HttpStatus.valueOf(ex.getResponseStatus()),
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaughtException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex,
                Errors.INTERNAL_SERVER_ERROR.setMsg(ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

}
