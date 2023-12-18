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
package demo.skr.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthApp.class)
@AutoConfigureMockMvc
@Transactional
@Rollback
public class AuthSignInTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignInWithoutTenent() throws Exception {
        mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "test")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("login-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                .andExpect(jsonPath("principal.tenentCode").doesNotExist());

        mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "guest"),
                        new BasicNameValuePair("password", "guest")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("login-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                .andExpect(jsonPath("principal.tenentCode").doesNotExist());
    }

    @Test
    public void testSignInWithTenent() throws Exception {
        mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("tenentCode", "org")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("login-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                // User for tenent should be created
                .andExpect(jsonPath("principal.username", equalTo("test")))
                .andExpect(jsonPath("principal.tenentCode", equalTo("org")));
    }

    @Test
    public void testSignInFailed() throws Exception {
        mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "asdf")
                )))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("ec", equalTo(ErrorInfo.BAD_CERTIFICATION.getCode())));;
    }

    @Test
    public void testLoginByToken() throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("tenentCode", "org")
                ))))).andReturn().getResponse().getContentAsByteArray());

        mvc.perform(post("/auth/sign-in-by-token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("loginToken", response.get("login-token").asText())
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                .andExpect(jsonPath("principal.tenentCode", equalTo("org")));
    }

    @Test
    public void testRefreshToken() throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("tenentCode", "org")
                ))))).andReturn().getResponse().getContentAsByteArray());

        mvc.perform(post("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("refreshToken", response.get("refresh-token").asText())
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                .andExpect(jsonPath("principal.tenentCode", equalTo("org")));
    }

    @Test
    public void testBindCertification() throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("tenentCode", "org")
                ))))).andReturn().getResponse().getContentAsByteArray());

        mvc.perform(post("/auth/bind-mobile")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("loginToken", response.get("login-token").asText()),
                        new BasicNameValuePair("mobile", "123456789"),
                        new BasicNameValuePair("captcha", "qwerty")
                )))))
                .andExpect(status().isOk());

        // bind again should be ok without saving.
        mvc.perform(post("/auth/bind-mobile")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                    new BasicNameValuePair("loginToken", response.get("login-token").asText()),
                    new BasicNameValuePair("mobile", "123456789"),
                    new BasicNameValuePair("captcha", "qwerty")
                )))))
            .andExpect(status().isOk());

        // sign in with new certification (without tenentCode)
        mvc.perform(post("/auth/sign-in-by-sms")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("mobile", "123456789"),
                        new BasicNameValuePair("captcha", "zxcvbn"),
                        new BasicNameValuePair("tenentCode", "org")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                .andExpect(jsonPath("principal.tenentCode", equalTo("org")));

        // sign in with new certification (with tenentCode)
        mvc.perform(post("/auth/sign-in-by-sms")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("mobile", "123456789"),
                        new BasicNameValuePair("captcha", "zxcvbn")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()))
                .andExpect(jsonPath("principal", notNullValue()))
                .andExpect(jsonPath("principal.tenentCode").doesNotExist());

        // unbind mobile
        mvc.perform(post("/auth/unbind-mobile")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("loginToken", response.get("login-token").asText()),
                        new BasicNameValuePair("mobile", "123456789")
                )))))
                .andExpect(status().isOk());

        // sign in failed with unbound certification
        mvc.perform(post("/auth/sign-in-by-sms")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("mobile", "123456789"),
                        new BasicNameValuePair("captcha", "zxcvbn"),
                        new BasicNameValuePair("tenentCode", "org")
                )))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("ec", equalTo(ErrorInfo.CERTIFICATION_NOT_FOUND.getCode())));


        // binding same identity for different certification scope should be supported.
        mvc.perform(post("/auth/bind-mobile")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("loginToken", response.get("login-token").asText()),
                        new BasicNameValuePair("mobile", "test"),
                        new BasicNameValuePair("captcha", "qwerty")
                )))))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnBindLastCertificationFailed() throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "test"),
                        new BasicNameValuePair("tenentCode", "org")
                ))))).andReturn().getResponse().getContentAsByteArray());

        // unbind mobile
        mvc.perform(post("/auth/unbind-username")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(List.of(
                        new BasicNameValuePair("loginToken", response.get("login-token").asText()),
                        new BasicNameValuePair("username", "test")
                )))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("ec", equalTo(ErrorInfo.LAST_CERTIFICATION.getCode())));
    }
}