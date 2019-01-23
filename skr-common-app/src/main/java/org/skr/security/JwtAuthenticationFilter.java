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
            Authentication authentication = getAuthentication(request,
                    securityProperties.getAccessToken().getHeader(),
                    securityProperties.getAccessToken().getPrefix(),
                    securityProperties.getAccessToken().getSecret());
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

    public static JwtAuthenticationToken getAuthentication(HttpServletRequest request,
                                                           String headerName,
                                                           String prefix,
                                                           String secret) {
        String accessToken = request.getHeader(headerName);
        return Optional.ofNullable(accessToken)
            .map(token -> token.replace(prefix, ""))
            .map(token -> JwtUtil.decode(token, secret))
            .map(decoded -> new JwtAuthenticationToken(BeanUtil.fromJSON(User.class, decoded)))
            .orElse(null);
    }

}
