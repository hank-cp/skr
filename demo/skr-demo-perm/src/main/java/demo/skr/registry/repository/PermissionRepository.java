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
package demo.skr.registry.repository;

import demo.skr.registry.model.PersistedPermission;
import demo.skr.registry.model.PersistedRealm;
import org.skr.common.util.Checker;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Repository
public interface PermissionRepository extends JpaRepository<PersistedPermission, String> {

    @Query("SELECT p FROM PersistedPermission p ORDER BY p.bit DESC")
    List<PersistedPermission> findByMaxBit(Pageable pageable);

    default PersistedPermission findMaxBitPermission() {
        List<PersistedPermission> resultList = findByMaxBit(PageRequest.of(0, 1));
        if (Checker.isEmpty(resultList)) return null;
        return resultList.get(0);
    }

    List<PersistedPermission> findByRealm(PersistedRealm realm);

    int countByRealmCode(String realmCode);

    @Query("SELECT p FROM PersistedPermission p WHERE p.disabled = false")
    List<PersistedPermission> findEnabledPermissions();

}
