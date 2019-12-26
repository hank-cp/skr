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
package org.skr.auth.service;

import lombok.NonNull;
import org.skr.common.exception.AuthException;
import org.skr.common.exception.BizException;
import org.skr.common.exception.ErrorInfo;
import org.skr.security.Certification;
import org.skr.security.SkrSecurityProperties;
import org.skr.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public abstract class AuthManager {

    @Autowired
    protected SkrSecurityProperties skrSecurityProperties;

    @Autowired
    private List<CertificationHandler> certificationHandlers;

    /**
     * Signing up a certification with <code>principal</code>. User profile could
     * be contained in <code>principal</code>
     */
    public final UserPrincipal signUp(UserPrincipal principal,
                                      @NonNull Certification certification,
                                      Map<String, Object> arguments) {
        return saveCertification(principal, certification, arguments);
    }

    /**
     * Given a <code>certification</code> to authenticate and retrieve
     * bound {@link UserPrincipal}
     */
    public final UserPrincipal signIn(@NonNull Certification certification,
                                      Map<String, Object> arguments) {
        UserPrincipal principal = authenticate(certification, arguments);
        if (principal == null) throw new AuthException(
                ErrorInfo.BAD_CERTIFICATION.msgArgs(certification.getIdentity()));
        return principal;
    }

    /**
     * Bind a new {@link Certification} to existed {@link UserPrincipal}
     * by providing valid bound {@link Certification}.
     */
    public final void bindCertification(@NonNull Certification boundCertification,
                                  @NonNull Certification newCertification,
                                  Map<String, Object> arguments) {
        UserPrincipal principal = authenticate(boundCertification, arguments);
        if (principal == null) {
            throw new AuthException(
                    ErrorInfo.BAD_CERTIFICATION.msgArgs(boundCertification.getIdentity()));
        }
        saveCertification(principal, newCertification, arguments);
    }

    /**
     * Unbind a {@link Certification} of existed {@link UserPrincipal}
     * by providing valid bound {@link Certification}.
     */
    public final void unbindCertification(@NonNull Certification boundCertification,
                                          @NonNull Certification removingCertification,
                                          Map<String, Object> arguments) {
        UserPrincipal principal = authenticate(boundCertification, arguments);
        if (principal == null) {
            throw new AuthException(
                    ErrorInfo.BAD_CERTIFICATION.msgArgs(boundCertification.getIdentity()));
        }
        removeCertification(principal, removingCertification);
    }

    protected UserPrincipal authenticate(@NonNull Certification certification,
                                         Map<String, Object> arguments) {
        for (CertificationHandler handler : certificationHandlers) {
            if (!handler.supports(certification)) continue;
            return handler.authenticate(certification, arguments);
        }
        return null;
    }

    protected UserPrincipal saveCertification(UserPrincipal principal,
                                              @NonNull Certification certification,
                                              Map<String, Object> arguments) {
        // certification identity must be unique
        for (CertificationHandler handler : certificationHandlers) {
            if (handler.findByIdentity(certification.getIdentity()) != null) {
                throw new AuthException(
                        ErrorInfo.CERTIFICATION_REGISTERED.msgArgs(certification.getIdentity()));
            }
        }

        for (CertificationHandler handler : certificationHandlers) {
            if (!handler.supports(certification)) continue;
            return handler.saveCertification(principal, certification, arguments);
        }
        return null;
    }

    protected void removeCertification(@NonNull UserPrincipal principal,
                                       @NonNull Certification certification) {
        List<Certification> boundCertifications = certificationHandlers.stream()
                .map(handler -> handler.getCertification(principal))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // last certification cannot be removed, otherwise the UserPrincipal
        // will be orphan.
        if (boundCertifications.size() == 1
                && Objects.equals(certification.getIdentity(),
                        boundCertifications.get(0).getIdentity())) {
            throw new BizException(ErrorInfo.LAST_CERTIFICATION.msgArgs(certification.getIdentity()));
        }

        for (CertificationHandler handler : certificationHandlers) {
            if (!handler.supports(certification)) continue;
            handler.removeCertification(principal, certification.getIdentity());
        }
    }
}