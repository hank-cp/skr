/*
 * Copyright (C) 2019-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.skr.auth.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import demo.skr.auth.model.User;
import demo.skr.auth.model.certification.RefreshTokenCertification;
import demo.skr.auth.repository.UserRepository;
import lombok.NonNull;
import org.skr.auth.service.CertificationHandler;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.JwtUtil;
import org.skr.security.Certification;
import org.skr.security.SkrSecurityProperties;
import org.skr.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Component
public class RefreshTokenCertificationHandler implements CertificationHandler {

    @Autowired
    private SkrSecurityProperties skrSecurityProperties;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(@NonNull Certification certification) {
        return certification instanceof RefreshTokenCertification;
    }

    @Override
    public UserPrincipal authenticate(@NonNull Certification certification,
                                      Map<String, Object> arguments) throws AuthException {
        RefreshTokenCertification refreshTokenCertification = (RefreshTokenCertification) certification;
        String payload;
        try {
            payload = Optional.of(refreshTokenCertification.refreshToken)
                    .map(token -> JwtUtil.decode(token,
                            skrSecurityProperties.getRefreshToken().getSecret()))
                    .orElse(null);
        } catch (TokenExpiredException ex) {
            throw new AuthException(ErrorInfo.REFRESH_TOKEN_EXPIRED);
        } catch (JWTVerificationException ex) {
            throw new AuthException(ErrorInfo.REFRESH_TOKEN_BROKEN);
        } catch (Exception ex) {
            throw new AuthException(ErrorInfo.AUTHENTICATION_REQUIRED);
        }
        String username = payload.split("∆")[0];
        String tenentCode = payload.split("∆")[1];

        User user = userRepository.findOneByTenentCodeAndUsername(tenentCode, username);
        return Optional.ofNullable(user).orElse(null);
    }

    @Override
    public Certification findByIdentity(@NonNull String certificationIdentity) {
        return null;
    }

    @Override
    public Certification getCertification(@NonNull UserPrincipal principal) {
        return null;
    }

    @Override
    public UserPrincipal saveCertification(@NonNull UserPrincipal principal, @NonNull Certification certification, Map<String, Object> arguments) {
        return principal;
    }

    @Override
    public void removeCertification(@NonNull UserPrincipal principal, @NonNull String certificationIdentity) {
    }
}