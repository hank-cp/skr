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
package demo.skr.aio.integration;

import demo.skr.aio.AioApp;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skr.common.exception.ErrorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AioApp.class)
@AutoConfigureMockMvc
@Transactional
@Rollback
public class AuthSignUpTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testSignUpWithoutTenent() throws Exception {
        mvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "asdf"),
                        new BasicNameValuePair("password", "asdf")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("login-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                .andExpect(jsonPath("principal.tenentCode").doesNotExist());

        mvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "asdf"),
                        new BasicNameValuePair("password", "qwer")
                )))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("ec", equalTo(ErrorInfo.CERTIFICATION_REGISTERED.getCode())));
    }

    @Test
    public void testSignUpWithTenent() throws Exception {
        mvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "asdf"),
                        new BasicNameValuePair("password", "asdf"),
                        new BasicNameValuePair("tenentCode", "org")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("login-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                // User for tenent should be created
                .andExpect(jsonPath("principal.username", equalTo("asdf")))
                .andExpect(jsonPath("principal.tenentCode", equalTo("org")));
    }

}