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
import org.springframework.web.util.NestedServletException;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
        JsonNode response = objectMapper.readTree(mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "dev"),
                        new BasicNameValuePair("password", "dev")
                ))))).andReturn().getResponse().getContentAsByteArray());

        mvc.perform(get("/task/list")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("access-token", response.get("access-token").asText())
                .content(EntityUtils.toString(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("auth_tenentCode", "org1"),
                        new BasicNameValuePair("username", "dev"),
                        new BasicNameValuePair("password", "dev")
                )))))
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

}