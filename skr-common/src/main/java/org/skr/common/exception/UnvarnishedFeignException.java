package org.skr.common.exception;

import feign.Response;
import feign.Util;
import org.skr.common.util.JsonUtil;
import org.skr.common.util.Checker;

public class UnvarnishedFeignException extends BaseException {

    private Errors errors;
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
        Errors errors = null;
        UnvarnishedFeignException exception;
        try {
            if (response.body() != null) {
                String errorJson = Util.toString(response.body().asReader());
                errors = JsonUtil.fromJSON(Errors.class, errorJson);
                if (Checker.isEmpty(errors.failedRpc)) {
                    // take methodKey if it hasn't not set in Exception handling chain.
                    errors.failedRpc = methodKey;
                }
            }
            exception = new UnvarnishedFeignException(errors.msg);
        } catch (Exception ex) {
            errors = Errors.INTERNAL_SERVER_ERROR.setMsg(ex.getMessage());
            exception = new UnvarnishedFeignException(errors.msg, ex);
        }
        exception.responseStatus = responseStatus;
        exception.errors = errors;
        return exception;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public Errors getErrors() {
        return errors;
    }
}

