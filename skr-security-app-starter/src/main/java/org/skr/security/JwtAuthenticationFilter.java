package org.skr.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.Errors;
import org.skr.common.util.Checker;
import org.skr.common.util.JsonUtil;
import org.skr.common.util.JwtUtil;
import org.skr.common.util.tuple.Tuple2;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Resolve {@link JwtAuthenticationToken} from request header
 *
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private SkrSecurityProperties skrSecurityProperties;

    private ApplicationContext applicationContext;

    public JwtAuthenticationFilter(SkrSecurityProperties skrSecurityProperties,
                                   ApplicationContext applicationContext) {
        super("/**");
        this.skrSecurityProperties = skrSecurityProperties;
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        setAuthenticationManager(new JwtAuthenticationManager());
        super.afterPropertiesSet();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String accessToken = request.getHeader(
                skrSecurityProperties.getAccessToken().getHeader());
        try {
            // try to resolve JwtAuthentication.
            return getAuthentication(accessToken);
        } catch (TokenExpiredException ex) {
            throw new AuthException(Errors.ACCESS_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(Errors.ACCESS_TOKEN_BROKEN);
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
//        super.successfulAuthentication(request, response, chain, authResult);
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(String accessToken) {
        String prefix;
        String secret;
        if (accessToken.startsWith(skrSecurityProperties.getAccessToken().getPrefix())) {
            prefix = skrSecurityProperties.getAccessToken().getPrefix();
            secret = skrSecurityProperties.getAccessToken().getSecret();
        } else if (accessToken.startsWith(skrSecurityProperties.getRobotToken().getPrefix())) {
            prefix = skrSecurityProperties.getRobotToken().getPrefix();
            secret = skrSecurityProperties.getRobotToken().getSecret();
        } else if (accessToken.startsWith(skrSecurityProperties.getTrainToken().getPrefix())) {
            prefix = skrSecurityProperties.getTrainToken().getPrefix();
            secret = skrSecurityProperties.getTrainToken().getSecret();
        } else {
            throw new AuthException(Errors.AUTHENTICATION_REQUIRED);
        }

        try {
            if (Checker.isEmpty(skrSecurityProperties.getJwtPrincipalClazz())) {
                throw new InternalAuthenticationServiceException(
                        "spring.skr.security.jwtPrincipalClazz is not specified.");
            }

            Class<?> principalClazz = applicationContext.getClassLoader()
                    .loadClass(skrSecurityProperties.getJwtPrincipalClazz());
            return Optional.of(accessToken)
                    .map(token -> token.replace(prefix, ""))
                    .map(token -> new Tuple2<>(JwtUtil.decode(token, secret), token))
                    .map(decodedTuple -> {
                        JwtPrincipal principal = JsonUtil.fromJSON(principalClazz, decodedTuple._0);
                        if (Checker.isTrue(principal.isRobot())) {
                            principal.setApiTrainJwtToken(accessToken);
                        } else {
                            principal.setApiTrainJwtToken(
                                    skrSecurityProperties.getTrainToken().getPrefix() +
                                            JwtUtil.encode(JsonUtil.toJSON(principal),
                                                    skrSecurityProperties.getTrainToken().getExpiration(),
                                                    skrSecurityProperties.getTrainToken().getSecret()));
                        }

                        return new JwtAuthenticationToken(principal);
                    })
                    .orElse(null);
        } catch (ClassNotFoundException ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

}
