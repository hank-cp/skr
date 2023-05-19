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
package org.skr.common.util.tuple;

import java.util.Objects;

/**
 * Scala style Tuple for Java
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public record Tuple4<T0, T1, T2, T3>(T0 _0, T1 _1, T2 _2, T3 _3) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple4<T0, T1, T2, T3> another = (Tuple4) o;

        return Objects.equals(_0, another._0)
            && Objects.equals(_1, another._1)
            && Objects.equals(_2, another._2)
            && Objects.equals(_3, another._3);

    }

    @Override
    public String toString() {
        return "(" + _0 + ',' + _1 + ',' + _2 + ',' + _3 + ')';
    }
}
