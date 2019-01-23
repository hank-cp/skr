package org.skr.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtUtil {

    public static String encode(String subject, long expiration, String secret) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(HMAC512(secret.getBytes()));
    }

    public static String decode(String token, String secret) {
        DecodedJWT decoded = JWT.require(HMAC512(secret.getBytes()))
                .build()
                .verify(token);
        if (decoded == null) return null;
        return decoded.getSubject();
    }

}
