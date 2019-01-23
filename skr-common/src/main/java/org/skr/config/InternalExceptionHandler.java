package org.skr.config;

import org.skr.common.Errors;
import org.skr.common.util.Apis;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class InternalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        Object standerBody = Optional.ofNullable(body)
                .orElse(Apis.apiResult(Errors.INTERNAL_SERVER_ERROR.setMsg(ex.getMessage())));
        return super.handleExceptionInternal(ex, standerBody, headers, status, request);
    }
}
