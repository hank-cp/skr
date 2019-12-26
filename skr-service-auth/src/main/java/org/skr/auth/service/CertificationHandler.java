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
import org.skr.security.Certification;
import org.skr.security.UserPrincipal;

import java.util.Map;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface CertificationHandler<Cert extends Certification> {

    /**
     * check if this handler could be apply to certain {@link Certification}
     */
    boolean supports(@NonNull Certification certification);

    /**
     * Authenticate given {@link Certification} and return corresponding
     * {@link UserPrincipal} to it.
     *
     * @param arguments extra argument to help locating {@link UserPrincipal}
     * @return {@link UserPrincipal} to given {@link Certification}
     * @throws AuthException if given {@link Certification} is not authenticated.
     */
    UserPrincipal authenticate(@NonNull Cert certification,
                               Map<String, Object> arguments)
            throws AuthException;

    /**
     * Find {@link Certification} by given certificationIdentity.
     * {@link Certification#getIdentity()} should be unique.
     *
     * @return {@link Certification} that has <code>certificationIdentity</code>
     */
    Cert findByIdentity(@NonNull String certificationIdentity);

    /**
     * @return {@link Certification} of given {@link UserPrincipal}
     */
    Cert getCertification(@NonNull UserPrincipal principal);

    /**
     * Tells that how to save given {@link Certification} and bind it to
     * {@link UserPrincipal}
     */
    UserPrincipal saveCertification(UserPrincipal principal,
                                    @NonNull Cert certification,
                                    Map<String, Object> arguments);

    /**
     * Remove a {@link Certification} for {@link UserPrincipal}
     * @param certificationIdentity of removing {@link Certification}
     */
    void removeCertification(@NonNull UserPrincipal principal,
                             @NonNull String certificationIdentity);
}
