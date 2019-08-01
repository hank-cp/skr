package org.skr.security;

import org.skr.common.exception.AuthException;
import org.skr.common.util.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthExceptionFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthException | NestedServletException ex) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            AuthException authEx = null;
            if (ex instanceof AuthException) {
                authEx = (AuthException) ex;
            }
            if (ex instanceof NestedServletException
                    && ex.getCause() instanceof AuthException) {
                authEx = (AuthException) ex.getCause();
            }
            if (authEx != null) {
                response.getWriter().write(JsonUtil.toJSON(authEx.getErrorInfo()));
            }
        }
    }
}
