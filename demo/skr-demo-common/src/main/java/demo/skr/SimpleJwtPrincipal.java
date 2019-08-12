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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.skr.security.JwtPrincipal;

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

    private long permissionBit1;

    private long permissionBit2;

    private long permissionBit3;

    private String nickName;

    private byte status;

    @Setter
    private String apiTrainJwtToken;

    private Boolean robot;

    private int vipLevel;

    @Override
    public Boolean isRobot() {
        return robot;
    }
}
