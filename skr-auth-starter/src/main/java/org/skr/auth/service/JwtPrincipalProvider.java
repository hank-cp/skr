package org.skr.auth.service;

import org.skr.security.JwtPrincipal;

import java.util.Map;

public interface JwtPrincipalProvider {

    JwtPrincipal loadJwtPrincipal(String username, Map<String, Object> params);

}
