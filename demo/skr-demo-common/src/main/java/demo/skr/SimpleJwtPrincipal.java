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
package demo.skr;

import lombok.*;
import org.skr.security.JwtPrincipal;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class SimpleJwtPrincipal implements JwtPrincipal {

    private String username;

    private String tenentCode;

    private long permissionBit;

    private byte status;

    @Setter
    private String apiTrainJwtToken;

    private Boolean ghost;

    private int vipLevel;

    @Override
    public @NotNull String getIdentity() {
        return username;
    }

    @Override
    public @NotNull String getName() {
        return username;
    }

    @Override
    public Boolean isGhost() {
        return ghost;
    }
}
