package org.skr.common.exception;

import feign.Response;
import feign.Util;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.Checker;

public class UnvarnishedFeignException extends BaseException {

    private Errors errors;
    private int responseStatus;

    private UnvarnishedFeignException() {}

    public static UnvarnishedFeignException build(String methodKey, Response response) {
        UnvarnishedFeignException exception = new UnvarnishedFeignException();
        exception.responseStatus = response.status();
        try {
            if (response.body() != null) {
                String errorJson = Util.toString(response.body().asReader());
                exception.errors = BeanUtil.fromJSON(Errors.class, errorJson);
                if (Checker.isEmpty(exception.errors.failedRpc)) {
                    // take methodKey if it hasn't not set in Exception handling chain.
                    exception.errors.failedRpc = methodKey;
                }
            }
        } catch (Exception ex) {
            exception.errors = Errors.INTERNAL_SERVER_ERROR.setMsg(ex.getMessage());
        }
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

