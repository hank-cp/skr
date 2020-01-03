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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class GhostJwtPrincipal implements JwtPrincipal {

    private String ghostUserName;

    @Override
    public Boolean isGhost() {
        return true;
    }

    @Override
    public String getIdentity() {
        return ghostUserName;
    }

    @Override
    public String getDisplayName() {
        return ghostUserName;
    }

}