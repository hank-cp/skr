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
package org.skr.common.exception;

import feign.Response;
import feign.Util;
import org.skr.common.util.Checker;
import org.skr.common.util.JsonUtil;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
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
                errorInfo = JsonUtil.fromJson(ErrorInfo.class, errorJson);
                if (Checker.isEmpty(errorInfo.getFailedRpc())) {
                    // take methodKey if it hasn't not set in Exception handling chain.
                    errorInfo.failedRpc(methodKey);
                }
            }
            exception = new UnvarnishedFeignException(errorInfo.getMsg());
        } catch (Exception ex) {
            errorInfo = ErrorInfo.INTERNAL_SERVER_ERROR.msgArgs(ex.getLocalizedMessage());
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

