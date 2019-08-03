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
package demo.skr.model.registry;

import demo.skr.model.BaseEntity;
import lombok.Getter;
import org.skr.common.util.Checker;
import org.skr.registry.EndPointRegistry;
import org.skr.registry.PermissionRegistry;
import org.skr.registry.RealmRegistry;

import javax.persistence.ElementCollection;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@MappedSuperclass
@Getter
public class EndPoint extends BaseEntity implements EndPointRegistry {

    @Id
    @NotNull
    public String url;

    @NotNull
    @Transient
    public String realmCode;

    @Transient
    public String permissionCode;

    @NotNull
    public String breadcrumb;

    public String label;

    public String description;

    @ElementCollection
    public List<String> relatedPermissionCodes;

    @Override
    public RealmRegistry getRealm() {
        Realm realm = new Realm();
        realm.code = realmCode;
        return realm;
    }

    @Override
    public @NotNull PermissionRegistry getPermission() {
        if (Checker.isEmpty(permissionCode)) return null;
        Permission permission = new Permission();
        permission.code = permissionCode;
        return permission;
    }

    public static EndPoint of(String permissionCode,
                              @NotNull String url,
                              @NotNull String breadcrumb) {
        EndPoint endPoint = new EndPoint();
        endPoint.url = url;
        endPoint.permissionCode = permissionCode;
        endPoint.breadcrumb = breadcrumb;
        return endPoint;
    }

}
