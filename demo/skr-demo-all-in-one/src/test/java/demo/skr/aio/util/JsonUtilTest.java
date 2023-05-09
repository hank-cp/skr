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
package demo.skr.aio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.skr.common.util.JsonUtil;
import org.skr.config.json.JsonSkipPersistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JsonUtilTest {

    public static class A {
        public int a;
        @JsonSkipPersistence
        public int b;

        public String getC() {
            return "c";
        }
        public void setC(String value) {
        }

        @JsonSkipPersistence
        public String getD() {
            return "d";
        }
        public void setD(String value) {
        }

        public String getE() {
            return "e";
        }
    }

    public record B(
        int a,
        @JsonSkipPersistence
        int b) {}

    @Test
    public void testSkipPersistence() {
        ObjectMapper objectMapper = JsonUtil.setupPersistentObjectMapper(JsonUtil.getObjectMapper());

        A a = new A();
        a.a = 1;
        a.b = 2;
        JsonNode json = JsonUtil.toJsonNode(objectMapper, a);
        assertThat(json.get("a"), notNullValue());
        assertThat(json.get("b"), nullValue());
        assertThat(json.get("c"), notNullValue());
        assertThat(json.get("d"), nullValue());
        assertThat(json.get("e"), nullValue());

        B b = new B(1, 2);
        json = JsonUtil.toJsonNode(objectMapper, b);
        assertThat(json.get("a"), notNullValue());
        assertThat(json.get("b"), nullValue());
    }
}