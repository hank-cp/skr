package org.skr.auth.controller;

import com.auth0.jwt.JWT;
import org.skr.auth.AuthTokenProperties;
import org.skr.auth.model.User;
import org.skr.auth.repository.UserRepository;
import org.skr.common.Constants;
import org.skr.common.Errors;
import org.skr.common.util.Apis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenProperties authTokenProperties;

    @PostMapping("/login")
    public @ResponseBody Map<String, Object> loginByUsernamePassword(
            @RequestParam String orgCode,
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response) {
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (auth.isAuthenticated()) {
            User user = userRepository.findOneByOrgCodeAndAccount(orgCode, username);

            if (user == null) return Apis.apiResult(Errors.ACCOUNT_NOT_BELONG_TO_ORG);
            if (user.status == Constants.DISABLED)
                return Apis.apiResult(Errors.USER_DISABLED);

            if (user.status == User.USER_STATUS_JOINING_NEED_APPROVAL) return Apis.apiResult(Errors.USER_NEED_APPROVAL);
            if (user.status == User.USER_STATUS_JOINING_REJECT)
                return Apis.apiResult(Errors.USER_REJECTED);

            org.skr.security.User commonUser = user.buildCommonUser();

            String token = JWT.create()
                    .withSubject(commonUser.username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + authTokenProperties.getExpiration()))
                    .sign(HMAC512(authTokenProperties.getSecret().getBytes()));
            response.addHeader(authTokenProperties.getHeader(), authTokenProperties.getPrefix() + token);
            return Apis.apiResult(Errors.OK);

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Apis.apiResult(Errors.NOT_AUTHENTICATED);
        }
    }

}
