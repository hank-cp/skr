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
package org.skr.registry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.skr.common.util.Checker;

import java.util.Objects;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor
public class SimpleRealm implements IRealm {

    @NonNull
    public String code;

    public int version;

    public IRealm.RealmStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        IRealm that = (IRealm) o;
        return !Checker.isEmpty(code) && Objects.equals(code, that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

}
