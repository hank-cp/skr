package org.skr.auth.controller;

import org.skr.auth.model.User;
import org.skr.auth.repository.UserRepository;
import org.skr.common.Constants;
import org.skr.common.Errors;
import org.skr.common.util.Apis;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.JwtUtil;
import org.skr.security.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityProperties securityProperties;

    @PostMapping("/login")
    public @ResponseBody Map<String, Object> loginByUsernamePassword(
            @RequestParam String orgCode,
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response) {
        Authentication auth = null;
        try {
            auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Apis.apiResult(Errors.NOT_AUTHENTICATED);
        }
        if (!auth.isAuthenticated()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Apis.apiResult(Errors.NOT_AUTHENTICATED);
        }

        User user = userRepository.findOneByOrgCodeAndAccount(orgCode, username);

        if (user == null) return Apis.apiResult(Errors.ACCOUNT_NOT_BELONG_TO_ORG);
        if (user.status == Constants.DISABLED)
            return Apis.apiResult(Errors.USER_DISABLED);

        if (user.status == User.USER_STATUS_JOINING_NEED_APPROVAL) return Apis.apiResult(Errors.USER_NEED_APPROVAL);
        if (user.status == User.USER_STATUS_JOINING_REJECT)
            return Apis.apiResult(Errors.USER_REJECTED);

        org.skr.security.User commonUser = user.buildCommonUser();

        String accessToken = JwtUtil.encode(BeanUtil.toJSON(commonUser),
                securityProperties.getAccessToken().getExpiration(),
                securityProperties.getAccessToken().getSecret());
        response.addHeader(securityProperties.getAccessToken().getHeader(),
                securityProperties.getAccessToken().getPrefix() + accessToken);

        String refreshToken = JwtUtil.encode(commonUser.username,
                securityProperties.getRefreshToken().getExpiration(),
                securityProperties.getRefreshToken().getSecret());
        response.addHeader(securityProperties.getRefreshToken().getHeader(),
                securityProperties.getRefreshToken().getPrefix() + refreshToken);

        return Apis.apiResult(Errors.OK);
    }

}
