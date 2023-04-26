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
package demo.skr.reg.model;

import demo.skr.model.BaseEntity;
import lombok.Getter;
import org.skr.registry.IEntityRegistry;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@MappedSuperclass
@Getter
public class EndPoint extends BaseEntity implements IEntityRegistry {

    public static final String BREADCRUMB_SEPARATOR = "\\.";

    @Id
    @NotNull
    public String url;

    @NotNull
    @Transient
    public String permissionCode;

    @NotNull
    public String breadcrumb;

    public String label;

    public String description;

    @Override
    public boolean isBuiltin() {
        return true;
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
