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
package org.skr.registry;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.exception.RegException;

import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Slf4j
public abstract class AbstractRegHost<RegistryPack extends IRegistryPack>
        implements IRegService<RegistryPack> {

    protected abstract StartedRealmStatus<RegistryPack> getRealmStatus(@NonNull String realmCode);

    /**
     * Manage realm status
     *
     * @param registryPack if it's persisted and retrieved by {@link #getRealmStatus(String)},
     *                     it will be available for {@link #doUnregister(String, IRegistryPack)}
     *                     as the second argument.
     */
    protected abstract void setRealmStatus(@NonNull String realmCode,
                                           @NonNull IRealm.RealmStatus status,
                                           String realmVersion,
                                           RegistryPack registryPack);

    protected abstract void doRegister(@NonNull String realmCode,
                                       String realmVersion,
                                       @NonNull RegistryPack registryPack);

    protected abstract void doUnregister(@NonNull String realmCode,
                                         RegistryPack registryPack);

    @Override
    public void register(@NonNull String realmCode,
                         String realmVersion,
                         @NonNull RegistryPack registryPack) {
        StartedRealmStatus<RegistryPack> realmStatus = getRealmStatus(realmCode);
        if (realmStatus != null && realmStatus.status == IRealm.RealmStatus.STARTED) {
            if (realmStatus.realmVersion != null && realmVersion != null
                    && Objects.equals(realmStatus.realmVersion, realmVersion)) {
                // if realm has no changes, return
                // if realmVersion is not provided, always re-register
                return;
            } else {
                // version changed, unregister first before register again
                try {
                    doUnregister(realmCode, registryPack);
                } catch (Exception ex) {
                    throw new RegException(ErrorInfo.UNREGISTER_REGISTRY_FAILED
                            .msgArgs(realmCode, ex.getMessage()), ex);
                }
            }
        }

        try {
            doRegister(realmCode, realmVersion, registryPack);
        } catch (Exception ex) {
            setRealmStatus(realmCode, IRealm.RealmStatus.ERROR, realmVersion, null);
            throw new RegException(ErrorInfo.REGISTER_REGISTRY_FAILED
                    .msgArgs(realmCode, ex.getMessage()), ex);
        }

        setRealmStatus(realmCode, IRealm.RealmStatus.STARTED, realmVersion, registryPack);
    }

    @Override
    public void unregister(@NonNull String realmCode) {
        StartedRealmStatus<RegistryPack> realmStatus = getRealmStatus(realmCode);
        if (realmStatus == null || realmStatus.status != IRealm.RealmStatus.STARTED) {
            // if realm is not started, return
        }

        try {
            doUnregister(realmCode,
                    Optional.ofNullable(realmStatus).map(status -> status.registryPack).orElse(null));
        } catch (Exception ex) {
            throw new RegException(ErrorInfo.UNREGISTER_REGISTRY_FAILED
                    .msgArgs(realmCode, ex.getMessage()), ex);
        }

        setRealmStatus(realmCode, IRealm.RealmStatus.STOPPED, null, null);
    }

}