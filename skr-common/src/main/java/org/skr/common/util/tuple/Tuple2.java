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

/**
 * Scala style Tuple for Java
 */
public class Tuple2<T0, T1> {

    public final T0 _0;
    public final T1 _1;

    public Tuple2(T0 _0, T1 _1) {
        this._0 = _0;
        this._1 = _1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2 tuple2 = (Tuple2) o;

        return !(_0 != null ? !_0.equals(tuple2._0) : tuple2._0 != null)
            && !(_1 != null ? !_1.equals(tuple2._1) : tuple2._1 != null);

    }

    @Override
    public int hashCode() {
        int result = _0 != null ? _0.hashCode() : 0;
        result = 31 * result + (_1 != null ? _1.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + _0 + ',' + _1 + ')';
    }
}
