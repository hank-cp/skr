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
package demo.skr.auth.model.certification;

import demo.skr.auth.model.Account;
import demo.skr.model.IdBasedEntity;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.skr.security.Certification;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Entity
@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UsernamePasswordCertification extends IdBasedEntity implements Certification {

    @OneToOne(optional = false)
    public Account account;

    @NonNull
    public String username;

    @NonNull
    public String password;

    @Override
    public String getIdentity() {
        return username;
    }

    @Override
    public String getUserPrincipalIdentity() {
        return account.getIdentity();
    }
}