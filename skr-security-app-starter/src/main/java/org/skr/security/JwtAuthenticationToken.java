package org.skr.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.validation.constraints.NotNull;
import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtPrincipal principal;

    public JwtAuthenticationToken(@NotNull JwtPrincipal principal) {
        super(Collections.emptyList());

        if (principal == null) {
            throw new IllegalArgumentException(
                    "Cannot pass null or empty values to constructor");
        }

        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
