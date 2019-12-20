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

    public static final ErrorInfo INVALID_SMS_CAPTCHA          = ErrorInfo.of(11104, "Invalid sms captcha.");
    public static final ErrorInfo UNBIND_CERTIFICATION_FAILED  = ErrorInfo.of(11105, "Unbind certification %s failed.");

    public static final ErrorInfo ACCOUNT_NOT_BELONG_TO_ORG    = ErrorInfo.of(11107, "Account %s does not belong to org %s.");
    public static final ErrorInfo ACCOUNT_DISABLED             = ErrorInfo.of(11108, "Account %s is disabled.");
    public static final ErrorInfo USER_DISABLED                = ErrorInfo.of(11109, "User %s is disabled.");
    public static final ErrorInfo USER_NEED_APPROVAL           = ErrorInfo.of(11110, "User %s need to be approved.");
    public static final ErrorInfo USER_REJECTED                = ErrorInfo.of(11111, "User %s joining get rejected.");

}