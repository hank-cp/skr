package org.skr.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.skr.common.Errors;
import org.skr.common.exception.AuthException;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private SecurityProperties securityProperties;

    JwtAuthenticationFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            String accessToken = request.getHeader(
                    securityProperties.getAccessToken().getHeader());
            Authentication authentication = getAuthentication(accessToken);
            if (!authentication.isAuthenticated()) {
                throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (TokenExpiredException ex) {
            throw new AuthException(Errors.ACCESS_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(Errors.ACCESS_TOKEN_BROKEN);
        } catch (Exception ex) {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }
    }

    public JwtAuthenticationToken getAuthentication(String accessToken) {
        String prefix;
        String secret;
        if (accessToken.startsWith(securityProperties.getAccessToken().getPrefix())) {
            prefix = securityProperties.getAccessToken().getPrefix();
            secret = securityProperties.getAccessToken().getSecret();
        } else if (accessToken.startsWith(securityProperties.getRobotToken().getPrefix())) {
            prefix = securityProperties.getRobotToken().getPrefix();
            secret = securityProperties.getRobotToken().getSecret();
        } else {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        return Optional.of(accessToken)
            .map(token -> token.replace(prefix, ""))
            .map(token -> JwtUtil.decode(token, secret))
            .map(decoded -> new JwtAuthenticationToken(BeanUtil.fromJSON(User.class, decoded)))
            .orElse(null);
    }

}
