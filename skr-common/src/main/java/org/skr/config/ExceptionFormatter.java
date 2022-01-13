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
package org.skr.config;

import org.skr.SkrProperties;
import org.skr.common.exception.BaseException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.Checker;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class ExceptionFormatter {

    @Autowired
    private SkrProperties skrProperties;

    public ErrorInfo convert(Throwable ex) {
        return convert(ex, null);
    }

    public ErrorInfo convert(Throwable ex, ErrorInfo templateErrorInfo) {
        return convert(ex, templateErrorInfo, skrProperties.isDebug());
    }

    public ErrorInfo convert(Throwable ex, ErrorInfo templateErrorInfo, boolean debug) {
        ErrorInfo errorInfo = handleSpecificException(ex, skrProperties.isDebug());

        // general handling
        if (errorInfo == null) {
            errorInfo = Optional.ofNullable(templateErrorInfo).orElse(ErrorInfo.INTERNAL_SERVER_ERROR);
            if (debug) {
                String errorMessage = ex.getMessage();
                if (Checker.isEmpty(errorMessage)) {
                    errorMessage = ex.getClass().getSimpleName();
                }
                errorInfo = errorInfo.msgArgs(errorMessage);
            }
        }

        if (debug && Checker.isEmpty(errorInfo.getException())) {
            errorInfo = errorInfo.exception(ex);
        }
        return errorInfo;
    }

    protected ErrorInfo handleSpecificException(Throwable ex, boolean debug) {
        if (ex instanceof BaseException) {
            return ((BaseException) ex).getErrorInfo();
        }
        return null;
    }
}