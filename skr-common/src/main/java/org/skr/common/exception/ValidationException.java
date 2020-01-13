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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The super class for exceptions
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class ValidationException extends RuntimeException {

    private final List<ErrorInfo> errorInfos;

    public ValidationException(@NotNull List<ErrorInfo> errorInfos) {
        super(errorInfos.stream()
                .map(ErrorInfo::getMsg)
                .collect(Collectors.joining("\n")));
        this.errorInfos = errorInfos;
    }

    public List<ErrorInfo> getErrorInfos() {
        return errorInfos;
    }

    public ErrorInfo.ErrorLevel worstErrorLevel() {
        return ErrorInfo.worstErrorLevel(errorInfos);
    }

}
