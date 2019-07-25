package org.skr.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtUtil {

    /**
     * Encode a JWT token
     *
     * @param subject content to encode to JWT token
     * @param expiration expiration time in minutes, if 0 never expired.
     * @param secret used to encode token
     * @return encoded string
     */
    public static String encode(String subject, long expiration, String secret) {
        JWTCreator.Builder builder = JWT.create()
                .withSubject(subject);
        if (expiration >= 0) {
            builder.withExpiresAt(new Date(System.currentTimeMillis() +
                    expiration * 60 * 1000));
        }
        return builder.sign(HMAC512(secret.getBytes()));
    }

    /**
     * Decode a JWT token
     *
     * @param token JWT token to decode
     * @param secret used to decode token
     * @return decoded content
     */
    public static String decode(String token, String secret) {
        DecodedJWT decoded = JWT.require(HMAC512(secret.getBytes()))
                .build()
                .verify(token);
        if (decoded == null) return null;
        return decoded.getSubject();
    }

}
