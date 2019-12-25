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
package demo.skr.auth.model;

import demo.skr.model.IdBasedEntity;
import org.skr.security.UserPrincipal;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Entity
public class Account extends IdBasedEntity implements UserPrincipal {

    /** 0=enabled; 1=disabled; 2=fault state; */
    public byte status;

    @Override
    public @NotNull String getIdentity() {
        return Long.toString(id);
    }

    @Override
    public @NotNull String getDisplayName() {
        return uid.toString();
    }
}
