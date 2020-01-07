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
package demo.skr;

import org.skr.common.exception.ErrorInfo;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public final class MyErrorInfo {

    public static final ErrorInfo END_POINT_REGISTERED          = ErrorInfo.of(11101, "error.END_POINT_REGISTERED",         ErrorInfo.ErrorLevel.FATAL);
    public static final ErrorInfo PERMISSION_REGISTERED         = ErrorInfo.of(11102, "error.PERMISSION_REGISTERED",         ErrorInfo.ErrorLevel.FATAL);
    public static final ErrorInfo INVALID_SMS_CAPTCHA           = ErrorInfo.of(11104, "error.INVALID_SMS_CAPTCHA");

}