package demo.skr.auth;

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
import java.util.concurrent.Callable;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthApp.class)
@AutoConfigureMockMvc
@Rollback
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLogin() throws Exception {
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "dev"),
                        new BasicNameValuePair("password", "dev")
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()))
                .andExpect(jsonPath("refresh-token", notNullValue()));
    }

    @Test
    public void testLoginFailed() throws Exception {
        mvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                            new BasicNameValuePair("auth_tenentCode", "org1"),
                            new BasicNameValuePair("username", "dev"),
                            new BasicNameValuePair("password", "123")
                    )))))
                    .andExpect(status().isForbidden());
    }

    @Test
    public void testRefreshToken() throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "dev"),
                        new BasicNameValuePair("password", "dev")
                ))))).andReturn().getResponse().getContentAsByteArray());

        mvc.perform(post("/refresh-token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("refreshToken", response.get("refresh-token").asText())
                )))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("access-token", notNullValue()));
    }

    public static Throwable exceptionOf(Callable<?> callable) {
        try {
            callable.call();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }
}