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
package demo.skr.aio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AioApp.class)
@AutoConfigureMockMvc
@Rollback
public class JwtAuthIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testJwtAuth() throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "dev"),
                        new BasicNameValuePair("password", "dev")
                ))))).andReturn().getResponse().getContentAsByteArray());

        mvc.perform(get("/task/list")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("access-token", response.get("access-token").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testJwtAuthFailed() throws Exception {
        mvc.perform(get("/task/list")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("access-token", "asdfasdf")
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "dev"),
                        new BasicNameValuePair("password", "dev")
                )))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUserDetail() throws Exception {
        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "guest"),
                        new BasicNameValuePair("password", "guest")
                )))))
                .andExpect(status().isUnauthorized());;
    }

    @Test
    public void testPermissionCheck() throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "test"),
                        new BasicNameValuePair("password", "test")
                ))))).andReturn().getResponse().getContentAsByteArray());

        mvc.perform(get("/task/list")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("access-token", response.get("access-token").asText()))
                .andExpect(status().isForbidden());
    }

}