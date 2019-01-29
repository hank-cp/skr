package org.skr.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.skr.common.exception.Errors;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ConfException;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.Checker;
import org.skr.common.util.JwtUtil;
import org.skr.common.util.tuple.Tuple2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private SkrSecurityProperties skrSecurityProperties;

    JwtAuthenticationFilter(SkrSecurityProperties skrSecurityProperties) {
        this.skrSecurityProperties = skrSecurityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            String accessToken = request.getHeader(
                    skrSecurityProperties.getAccessToken().getHeader());
            Authentication authentication = getAuthentication(accessToken);
            if (!authentication.isAuthenticated()) {
                throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (TokenExpiredException ex) {
            throw new AuthException(Errors.ACCESS_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(Errors.ACCESS_TOKEN_BROKEN);
        } catch (Exception ex) {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        filterChain.doFilter(request, response);
    }

    public JwtAuthenticationToken getAuthentication(String accessToken) {
        String prefix;
        String secret;
        if (accessToken.startsWith(skrSecurityProperties.getAccessToken().getPrefix())) {
            prefix = skrSecurityProperties.getAccessToken().getPrefix();
            secret = skrSecurityProperties.getAccessToken().getSecret();
        } else if (accessToken.startsWith(skrSecurityProperties.getRobotToken().getPrefix())) {
            prefix = skrSecurityProperties.getRobotToken().getPrefix();
            secret = skrSecurityProperties.getRobotToken().getSecret();
        } else if (accessToken.startsWith(skrSecurityProperties.getGhostToken().getPrefix())) {
            prefix = skrSecurityProperties.getGhostToken().getPrefix();
            secret = skrSecurityProperties.getGhostToken().getSecret();
        } else {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        try {
            Class<?> principalClazz = Class.forName(skrSecurityProperties.getJwtPrincipalClazz());
            return Optional.of(accessToken)
                    .map(token -> token.replace(prefix, ""))
                    .map(token -> new Tuple2<>(JwtUtil.decode(token, secret), token))
                    .map(decodedTuple -> {
                        JwtPrincipal principal = BeanUtil.fromJSON(principalClazz, decodedTuple._0);
                        if (Checker.isTrue(principal.isRobot())) {
                            principal.setServiceJwtToken(accessToken);
                        } else {
                            principal.setServiceJwtToken(
                                    skrSecurityProperties.getGhostToken().getPrefix() +
                                    JwtUtil.encode(BeanUtil.toJSON(principal),
                                        skrSecurityProperties.getGhostToken().getExpiration(),
                                        skrSecurityProperties.getGhostToken().getSecret()));
                        }

                        return new JwtAuthenticationToken(principal);
                    })
                    .orElse(null);
        } catch (ClassNotFoundException ex) {
            throw new ConfException(Errors.INTERNAL_SERVER_ERROR, ex);
        }
    }

}
