package org.skr.auth.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.skr.auth.model.User;
import org.skr.auth.repository.UserRepository;
import org.skr.common.Constants;
import org.skr.common.exception.Errors;
import org.skr.common.exception.AuthException;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.JwtUtil;
import org.skr.security.JwtPrincipal;
import org.skr.security.SkrSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    private SkrSecurityProperties skrSecurityProperties;

    @PostMapping("/login")
    public @ResponseBody Map<String, Object> loginByUsernamePassword(
            @RequestParam String orgCode,
            @RequestParam String username,
            @RequestParam String password) {
        Authentication auth;
        try {
            auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException ex) {
            throw new AuthException(Errors.NOT_AUTHENTICATED);
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

        JwtPrincipal principal = user.buildJwtPrincipal();

        String accessToken = JwtUtil.encode(BeanUtil.toJSON(principal),
                skrSecurityProperties.getAccessToken().getExpiration(),
                skrSecurityProperties.getAccessToken().getSecret());
        String refreshToken = JwtUtil.encode(principal.username,
                skrSecurityProperties.getRefreshToken().getExpiration(),
                skrSecurityProperties.getRefreshToken().getSecret());

        return map(
            entry(skrSecurityProperties.getAccessToken().getHeader(),
                    skrSecurityProperties.getAccessToken().getPrefix() + accessToken),
            entry(skrSecurityProperties.getRefreshToken().getHeader(),
                    skrSecurityProperties.getRefreshToken().getPrefix() + refreshToken)
        );
    }

    @PostMapping("/refresh-token")
    public @ResponseBody Map<String, Object> refreshToken(
            @RequestParam String orgCode,
            @RequestParam String refreshToken) {

        String refreshPrefix = skrSecurityProperties.getRefreshToken().getPrefix();
        String refreshSecret = skrSecurityProperties.getRefreshToken().getSecret();

        String username;
        try {
            username = Optional.of(refreshToken)
                    .map(token -> token.replace(refreshPrefix, ""))
                    .map(token -> JwtUtil.decode(token, refreshSecret))
                    .orElse(null);
        } catch (TokenExpiredException ex) {
            throw new AuthException(Errors.REFRESH_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(Errors.REFRESH_TOKEN_BROKEN);
        } catch (Exception ex) {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        if (username == null) {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        User user = userRepository.findOneByOrgCodeAndAccount(orgCode, username);

        if (user == null) throw new AuthException(Errors.ACCOUNT_NOT_BELONG_TO_ORG);
        if (user.status == Constants.DISABLED)
            throw new AuthException(Errors.USER_DISABLED);
        if (user.status == User.USER_STATUS_JOINING_NEED_APPROVAL)
            throw new AuthException(Errors.USER_NEED_APPROVAL);
        if (user.status == User.USER_STATUS_JOINING_REJECT)
            throw new AuthException(Errors.USER_REJECTED);

        // refresh user info
        JwtPrincipal commonUser = user.buildJwtPrincipal();

        String accessToken = JwtUtil.encode(BeanUtil.toJSON(commonUser),
                skrSecurityProperties.getAccessToken().getExpiration(),
                skrSecurityProperties.getAccessToken().getSecret());

        Map<String, Object> result = map(
            entry(skrSecurityProperties.getAccessToken().getHeader(),
                    skrSecurityProperties.getAccessToken().getPrefix() + accessToken)
        );

        // renew refresh token
        if (skrSecurityProperties.isRenewRefreshToken()) {
            String newRefreshToken = JwtUtil.encode(commonUser.username,
                    skrSecurityProperties.getRefreshToken().getExpiration(),
                    skrSecurityProperties.getRefreshToken().getSecret());
            result.put(skrSecurityProperties.getRefreshToken().getHeader(),
                    skrSecurityProperties.getRefreshToken().getPrefix() + newRefreshToken);
        }

        return result;
    }

}
