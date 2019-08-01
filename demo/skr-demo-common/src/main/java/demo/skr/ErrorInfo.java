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

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface ErrorInfo extends org.skr.common.exception.ErrorInfo {

    org.skr.common.exception.ErrorInfo ACCOUNT_NOT_BELONG_TO_ORG    = new ErrorInfoImpl(1107, "Account does not belong to org.");
    org.skr.common.exception.ErrorInfo USER_DISABLED                = new ErrorInfoImpl(1108, "User is disabled.");
    org.skr.common.exception.ErrorInfo USER_NEED_APPROVAL           = new ErrorInfoImpl(1109, "User need to be approved.");
    org.skr.common.exception.ErrorInfo USER_REJECTED                = new ErrorInfoImpl(1110, "User joining get rejected.");
}