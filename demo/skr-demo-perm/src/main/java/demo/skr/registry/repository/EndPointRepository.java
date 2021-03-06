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

import demo.skr.registry.model.PersistedEndPoint;
import demo.skr.registry.model.PersistedRealm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Repository
public interface EndPointRepository extends JpaRepository<PersistedEndPoint, String> {

    List<PersistedEndPoint> findByRealm(PersistedRealm realm);

    int countByRealmCode(String realmCode);

    @Query("SELECT e FROM PersistedEndPoint e WHERE e.disabled = false")
    List<PersistedEndPoint> findEnabledEndPoints();
}
