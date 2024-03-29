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

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class AuthException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = -1412637398991745466L;

    private final ErrorInfo errorInfo;

    public AuthException(ErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
    }

    public AuthException(ErrorInfo errorInfo, Throwable e) {
        super(errorInfo.getMessage(), e);
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}

