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

import org.skr.common.exception.Errors;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class DemoErrors {

    public static final Errors ACCOUNT_NOT_BELONG_TO_ORG    = new Errors(1106, "Account does not belong to org.");
    public static final Errors USER_DISABLED                = new Errors(1107, "User is disabled.");
    public static final Errors USER_NEED_APPROVAL           = new Errors(1108, "User need to be approved.");
    public static final Errors USER_REJECTED                = new Errors(1109, "User joining get rejected.");
}