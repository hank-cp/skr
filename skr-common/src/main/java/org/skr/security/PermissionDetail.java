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

import org.apache.commons.lang3.NotImplementedException;
import org.skr.config.json.IntValuedEnum;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface PermissionDetail {

    enum PermissionResult implements IntValuedEnum {
        PERMISSION_GRANTED(0), PERMISSION_DENIED(1), PERMISSION_LIMITATION(2);

        private final int value;

        PermissionResult(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return value;
        }

        public static PermissionResult parse(int value) {
            for (PermissionResult item : PermissionResult.values()) {
                if (item.value() != value) continue;
                return item;
            }
            return PERMISSION_DENIED;
        }
    }

    String getCode();

    default PermissionResult checkAuthorization(JwtPrincipal principal) {
        throw new NotImplementedException("not implemented yet");
    }

}
