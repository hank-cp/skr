package org.skr.auth.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.skr.auth.model.User;
import org.skr.auth.repository.UserRepository;
import org.skr.common.Constants;
import org.skr.common.Errors;
import org.skr.common.exception.AuthException;
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
import java.util.Optional;

import static org.skr.common.util.CollectionUtils.*;

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
        Authentication auth;
        try {
            auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Apis.apiResult(Errors.NOT_AUTHENTICATED);
        }
        if (!auth.isAuthenticated()) {
            throw new AuthException(Errors.NOT_AUTHENTICATED);
        }

        User user = userRepository.findOneByOrgCodeAndAccount(orgCode, username);

        if (user == null) throw new AuthException(Errors.ACCOUNT_NOT_BELONG_TO_ORG);
        if (user.status == Constants.DISABLED)
            throw new AuthException(Errors.USER_DISABLED);
        if (user.status == User.USER_STATUS_JOINING_NEED_APPROVAL)
            throw new AuthException(Errors.USER_NEED_APPROVAL);
        if (user.status == User.USER_STATUS_JOINING_REJECT)
            throw new AuthException(Errors.USER_REJECTED);

        org.skr.security.User commonUser = user.buildCommonUser();

        String accessToken = JwtUtil.encode(BeanUtil.toJSON(commonUser),
                securityProperties.getAccessToken().getExpiration(),
                securityProperties.getAccessToken().getSecret());
        String refreshToken = JwtUtil.encode(commonUser.username,
                securityProperties.getRefreshToken().getExpiration(),
                securityProperties.getRefreshToken().getSecret());

        return Apis.apiResult(Errors.OK, map(
                entry(securityProperties.getAccessToken().getHeader(),
                        securityProperties.getAccessToken().getPrefix() + accessToken),
                entry(securityProperties.getRefreshToken().getHeader(),
                        securityProperties.getRefreshToken().getPrefix() + refreshToken))
        );
    }

    @PostMapping("/refreshToken")
    public @ResponseBody Map<String, Object> loginByUsernamePassword(
            @RequestParam String refreshToken) {

        String refreshPrefix = securityProperties.getRefreshToken().getPrefix();
        String refreshSecret = securityProperties.getRefreshToken().getSecret();

        org.skr.security.User commonUser;
        try {
            commonUser = Optional.of(refreshToken)
                    .map(token -> token.replace(refreshPrefix, ""))
                    .map(token -> JwtUtil.decode(token, refreshSecret))
                    .map(decoded -> (org.skr.security.User)
                            BeanUtil.fromJSON(org.skr.security.User.class, decoded))
                    .orElse(null);
        } catch (TokenExpiredException ex) {
            throw new AuthException(Errors.REFRESH_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(Errors.REFRESH_TOKEN_BROKEN);
        } catch (Exception ex) {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        if (commonUser == null) {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        User user = userRepository.findOneByOrgCodeAndAccount(
                commonUser.organization.code, commonUser.username);

        if (user == null) throw new AuthException(Errors.ACCOUNT_NOT_BELONG_TO_ORG);
        if (user.status == Constants.DISABLED)
            throw new AuthException(Errors.USER_DISABLED);
        if (user.status == User.USER_STATUS_JOINING_NEED_APPROVAL)
            throw new AuthException(Errors.USER_NEED_APPROVAL);
        if (user.status == User.USER_STATUS_JOINING_REJECT)
            throw new AuthException(Errors.USER_REJECTED);

        // refresh user info
        commonUser = user.buildCommonUser();

        String accessToken = JwtUtil.encode(BeanUtil.toJSON(commonUser),
                securityProperties.getAccessToken().getExpiration(),
                securityProperties.getAccessToken().getSecret());
        String newRefreshToken = JwtUtil.encode(commonUser.username,
                securityProperties.getRefreshToken().getExpiration(),
                securityProperties.getRefreshToken().getSecret());

        return Apis.apiResult(Errors.OK, map(
                entry(securityProperties.getAccessToken().getHeader(),
                        securityProperties.getAccessToken().getPrefix() + accessToken),
                entry(securityProperties.getRefreshToken().getHeader(),
                        securityProperties.getRefreshToken().getPrefix() + newRefreshToken))
                );
    }

}
