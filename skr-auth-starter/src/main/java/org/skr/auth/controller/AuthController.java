package org.skr.auth.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.skr.auth.service.JwtPrincipalProvider;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.JsonUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.skr.common.util.CollectionUtils.*;

@RestController
public class AuthController {

    public static final String EXTRA_PARAM_PREFIX = "auth_";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtPrincipalProvider jwtPrincipalProvider;

    @Autowired
    private SkrSecurityProperties skrSecurityProperties;

    @PostMapping("/login")
    public @ResponseBody Map<String, Object> loginByUsernamePassword(
            @RequestParam String username, @RequestParam String password,
            HttpServletRequest request) {
        Authentication auth;
        try {
            auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException ex) {
            throw new AuthException(ErrorInfo.NOT_AUTHENTICATED);
        }
        if (!auth.isAuthenticated()) {
            throw new AuthException(ErrorInfo.NOT_AUTHENTICATED);
        }

        JwtPrincipal principal = jwtPrincipalProvider.loadJwtPrincipal(
                username, resolveAuthExtraParams(request));
        String accessToken = JwtUtil.encode(JsonUtil.toJSON(principal),
                skrSecurityProperties.getAccessToken().getExpiration(),
                skrSecurityProperties.getAccessToken().getSecret());
        String refreshToken = JwtUtil.encode(principal.getUsername(),
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
            @RequestParam String refreshToken, HttpServletRequest request) {

        String refreshPrefix = skrSecurityProperties.getRefreshToken().getPrefix();
        String refreshSecret = skrSecurityProperties.getRefreshToken().getSecret();

        String username;
        try {
            username = Optional.of(refreshToken)
                    .map(token -> token.replace(refreshPrefix, ""))
                    .map(token -> JwtUtil.decode(token, refreshSecret))
                    .orElse(null);
        } catch (TokenExpiredException ex) {
            throw new AuthException(ErrorInfo.REFRESH_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(ErrorInfo.REFRESH_TOKEN_BROKEN);
        } catch (Exception ex) {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }

        if (username == null) {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }

        JwtPrincipal principal = jwtPrincipalProvider.loadJwtPrincipal(
                username, resolveAuthExtraParams(request));
        String accessToken = JwtUtil.encode(JsonUtil.toJSON(principal),
                skrSecurityProperties.getAccessToken().getExpiration(),
                skrSecurityProperties.getAccessToken().getSecret());

        Map<String, Object> result = map(
            entry(skrSecurityProperties.getAccessToken().getHeader(),
                    skrSecurityProperties.getAccessToken().getPrefix() + accessToken)
        );

        // renew refresh token
        if (skrSecurityProperties.isRenewRefreshToken()) {
            String newRefreshToken = JwtUtil.encode(principal.getUsername(),
                    skrSecurityProperties.getRefreshToken().getExpiration(),
                    skrSecurityProperties.getRefreshToken().getSecret());
            result.put(skrSecurityProperties.getRefreshToken().getHeader(),
                    skrSecurityProperties.getRefreshToken().getPrefix() + newRefreshToken);
        }

        return result;
    }

    private Map<String, Object> resolveAuthExtraParams(HttpServletRequest request) {
        Map<String, Object> extraParams = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (paramName.startsWith(EXTRA_PARAM_PREFIX)) {
                extraParams.put(paramName.replace(EXTRA_PARAM_PREFIX, ""),
                        request.getParameter(paramName));
            }
        }
        return extraParams;
    }

}