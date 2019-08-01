package org.skr.common.exception;

import feign.Response;
import feign.Util;
import org.skr.common.util.JsonUtil;
import org.skr.common.util.Checker;

public class UnvarnishedFeignException extends BaseException {

    private ErrorInfo errorInfo;
    private int responseStatus;

    private UnvarnishedFeignException() {}

    public UnvarnishedFeignException(String message) {
        super(message);
    }

    public UnvarnishedFeignException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UnvarnishedFeignException build(String methodKey, Response response) {
        int responseStatus = response.status();
        ErrorInfo errorInfo = null;
        UnvarnishedFeignException exception;
        try {
            if (response.body() != null) {
                String errorJson = Util.toString(response.body().asReader());
                errorInfo = JsonUtil.fromJSON(ErrorInfo.class, errorJson);
                if (Checker.isEmpty(errorInfo.getFailedRpc())) {
                    // take methodKey if it hasn't not set in Exception handling chain.
                    errorInfo.setFailedRpc(methodKey);
                }
            }
            exception = new UnvarnishedFeignException(errorInfo.getMsg());
        } catch (Exception ex) {
            errorInfo = ErrorInfo.INTERNAL_SERVER_ERROR_INFO.setMsg(ex.getMessage());
            exception = new UnvarnishedFeignException(errorInfo.getMsg(), ex);
        }
        exception.responseStatus = responseStatus;
        exception.errorInfo = errorInfo;
        return exception;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}

