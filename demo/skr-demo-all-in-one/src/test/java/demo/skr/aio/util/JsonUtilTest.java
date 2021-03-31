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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.skr.aio.AioApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.JsonUtil;
import org.skr.config.json.JsonSkipPersistence;
import org.skr.security.PermissionDetail;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.skr.common.util.JsonUtil.JSON_FILTER_SKIP_PERSISTENCE;

public class JsonUtilTest {

    @JsonFilter(JSON_FILTER_SKIP_PERSISTENCE)
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
    }

    @Test
    public void testSkipPersistence() {
        A a = new A();
        a.a = 1;
        a.b = 2;
        ObjectMapper objectMapper = JsonUtil.setupPersistentObjectMapper(JsonUtil.getObjectMapper());
        JsonNode json = JsonUtil.toJsonNode(objectMapper, a);
        assertThat(json.get("a"), notNullValue());
        assertThat(json.get("b"), nullValue());
        assertThat(json.get("c"), notNullValue());
        assertThat(json.get("d"), nullValue());
    }
}