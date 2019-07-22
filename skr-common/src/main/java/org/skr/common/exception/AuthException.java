package org.skr.common.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthException extends AuthenticationException {

    private Errors errors;

    public AuthException(Errors errors) {
        super(errors.msg);
        this.errors = errors;
    }

    public AuthException(Errors errors, Throwable e) {
        super(errors.msg + ": " + e.getMessage(), e);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}

