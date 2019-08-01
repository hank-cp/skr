package org.skr.common.exception;

import javax.validation.constraints.NotNull;

public class ConfException extends BaseException {

    private ErrorInfo errorInfo;

    public ConfException(@NotNull ErrorInfo errorInfo) {
        super(errorInfo.getMsg());
        this.errorInfo = errorInfo;
    }

    public ConfException(@NotNull ErrorInfo errorInfo, Throwable e) {
        super(errorInfo.getMsg() + ": " + e.getMessage(), e);
        this.errorInfo = errorInfo;
    }

    @Override
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}

