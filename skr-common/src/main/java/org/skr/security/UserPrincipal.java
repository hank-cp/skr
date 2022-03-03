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
package org.skr.security;

import java.security.Principal;

/**
 * User identification in the actual system
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface UserPrincipal extends Principal {

    /**
     * Unique identity of this principal. Authentication
     * services must be able to locate unique actual
     * User principal via this identity.
     */
    String getIdentity();
}