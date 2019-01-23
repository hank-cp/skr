package org.skr.common.exception;

import org.skr.common.Errors;

import javax.validation.constraints.NotNull;

public class BizException extends BaseException {

    private Errors errors;

    public BizException(@NotNull Errors errors) {
        super(errors.msg);
        this.errors = errors;
    }

    public BizException(@NotNull Errors errors, Throwable e) {
        super(errors.msg + ": " + e.getMessage(), e);
        this.errors = errors;
    }

    @Override
    public Errors getErrors() {
        return errors;
    }
}

