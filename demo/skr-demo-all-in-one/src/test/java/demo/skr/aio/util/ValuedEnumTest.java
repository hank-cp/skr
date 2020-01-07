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

import demo.skr.registry.RegistryApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.JsonUtil;
import org.skr.security.PermissionDetail;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RegistryApp.class)
@AutoConfigureMockMvc
@Transactional
@Rollback
public class ValuedEnumTest {

    @Test
    @Transactional
    public void testStringEnumConversion() {
        ErrorInfo errorInfo = ErrorInfo.INTERNAL_SERVER_ERROR.msgArgs("test");
        String json = JsonUtil.toJson(errorInfo);
        assertThat(json, notNullValue());
        assertThat(json, containsString("fatal"));

        ErrorInfo deserializedErrorInfo = JsonUtil.fromJson(ErrorInfo.class, json);
        assertThat(deserializedErrorInfo, notNullValue());
        assertThat(deserializedErrorInfo.getLevel(), equalTo(ErrorInfo.ErrorLevel.FATAL));
    }

    @Test
    @Transactional
    public void testIntegerEnumConversion() {
        String json = JsonUtil.toJson(PermissionDetail.PermissionResult.PERMISSION_LIMITED);
        assertThat(json, notNullValue());
        assertThat(json, equalTo("2"));

        PermissionDetail.PermissionResult deserializedObj =
                JsonUtil.fromJson(PermissionDetail.PermissionResult.class, json);
        assertThat(deserializedObj, notNullValue());
        assertThat(deserializedObj, equalTo(PermissionDetail.PermissionResult.PERMISSION_LIMITED));
    }

}