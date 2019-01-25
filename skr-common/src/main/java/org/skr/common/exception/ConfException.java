package org.skr.common.exception;

import org.skr.common.Errors;

import javax.validation.constraints.NotNull;

public class ConfException extends BaseException {

    private Errors errors;

    public ConfException(@NotNull Errors errors) {
        super(errors.msg);
        this.errors = errors;
    }

    public ConfException(@NotNull Errors errors, Throwable e) {
        super(errors.msg + ": " + e.getMessage(), e);
        this.errors = errors;
    }

    @Override
    public Errors getErrors() {
        return errors;
    }
}

