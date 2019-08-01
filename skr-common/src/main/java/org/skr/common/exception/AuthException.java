package org.skr.common.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthException extends AuthenticationException {

    private ErrorInfo errorInfo;

    public AuthException(ErrorInfo errorInfo) {
        super(errorInfo.getMsg());
        this.errorInfo = errorInfo;
    }

    public AuthException(ErrorInfo errorInfo, Throwable e) {
        super(errorInfo.getMsg() + ": " + e.getMessage(), e);
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}

