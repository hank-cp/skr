/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skr.config;

import lombok.extern.slf4j.Slf4j;
import org.skr.common.exception.*;
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

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        Object standerBody = Optional.ofNullable(body)
                .orElse(ErrorInfo.INTERNAL_SERVER_ERROR.msgArgs(ex.getMessage()));
        return super.handleExceptionInternal(ex, standerBody, headers, status, request);
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Object> handleException(BizException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                ex.getErrorInfo(),
                new HttpHeaders(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handleException(AuthException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                ex.getErrorInfo(),
                new HttpHeaders(),
                HttpStatus.UNAUTHORIZED,
                request);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<Object> handleException(PermissionException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                ex.getErrorInfo(),
                new HttpHeaders(),
                HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler(UnvarnishedFeignException.class)
    public ResponseEntity<Object> handleException(UnvarnishedFeignException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                ex.getErrorInfo(),
                new HttpHeaders(),
                HttpStatus.valueOf(ex.getResponseStatus()),
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaughtException(Exception ex, WebRequest request) {
        log.error(getStackTrace(ex));
        return handleExceptionInternal(ex,
                ErrorInfo.INTERNAL_SERVER_ERROR
                        .msgArgs(ex.getMessage())
                        .exception(ex),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

}
