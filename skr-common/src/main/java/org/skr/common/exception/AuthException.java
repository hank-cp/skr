package org.skr.common.exception;

import org.skr.common.Errors;

public class AuthException extends BaseException {

    private Errors errors;

    public AuthException(Errors errors) {
        super(errors.msg);
        this.errors = errors;
    }

    public AuthException(Errors errors, Throwable e) {
        super(errors.msg + ": " + e.getMessage(), e);
        this.errors = errors;
    }

    @Override
    public Errors getErrors() {
        return errors;
    }
}

