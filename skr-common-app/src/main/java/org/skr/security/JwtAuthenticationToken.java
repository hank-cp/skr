package org.skr.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.validation.constraints.NotNull;
import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final User user;

    public JwtAuthenticationToken(@NotNull User user) {
        super(Collections.emptyList());

        if (user == null) {
            throw new IllegalArgumentException(
                    "Cannot pass null or empty values to constructor");
        }

        this.user = user;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }
}
