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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.skr.common.util.JsonUtil;
import org.skr.config.json.JsonSkipPersistence;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JsonUtilTest {

    public static class A {
        public int a;
        @JsonSkipPersistence
        public int b;
        @JsonIgnore
        public int f;

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

    public static class C {
        public Optional<Integer> a = Optional.empty();

        public Optional<Integer> b;
    }

    @Test
    public void testSkipPersistence() {
        ObjectMapper objectMapper = JsonUtil.setupPersistentObjectMapper(JsonUtil.getObjectMapper());

        A a = new A();
        a.a = 1;
        a.b = 2;
        a.f = 3;
        JsonNode json = JsonUtil.toJsonNode(objectMapper, a);
        assertThat(json.get("a"), notNullValue());
        assertThat(json.get("b"), nullValue());
        assertThat(json.get("c"), notNullValue());
        assertThat(json.get("d"), nullValue());
        assertThat(json.get("e"), nullValue());
        assertThat(json.get("f"), nullValue());

        B b = new B(1, 2);
        json = JsonUtil.toJsonNode(objectMapper, b);
        assertThat(json.get("a"), notNullValue());
        assertThat(json.get("b"), nullValue());
    }

    @Test
    public void testOptional() {
        ObjectMapper objectMapper = JsonUtil.setupPersistentObjectMapper(JsonUtil.getObjectMapper());

        C c1 = new C();
        c1.a = Optional.of(1);
        JsonNode json = JsonUtil.toJsonNode(objectMapper, c1);
        assertThat(json.get("a"), notNullValue());
        assertThat(json.get("a").asInt(), equalTo(1));

        C c2 = new C();
        json = JsonUtil.toJsonNode(objectMapper, c2);
        assertThat(json.get("a"), equalTo(NullNode.getInstance()));
        assertThat(json.get("b"), nullValue());

        C c3 = objectMapper.convertValue(json, C.class);
        assertThat(c3.a, equalTo(Optional.empty()));
        assertThat(c3.b, nullValue());

        ObjectNode c4Json = objectMapper.createObjectNode();
        C c4 = objectMapper.convertValue(c4Json.put("a", "1"), C.class);
        assertThat(c4.a, notNullValue());
        assertThat(c4.a.get(), equalTo(1));
    }
}