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

import demo.skr.SimpleJwtPrincipal;
import demo.skr.model.IdBasedEntity;
import org.skr.security.JwtPrincipal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@Entity
@Table(name = "t_user")
public class User extends IdBasedEntity {

    public static final byte USER_STATUS_JOINING_NEED_APPROVAL = 2;
    public static final byte USER_STATUS_JOINING_REJECT = 3;

    @NotNull
    @ManyToOne
    public Tenent tenent;

    @NotNull
    @ManyToOne
    public Account account;

    private long permissionBit1;

    private long permissionBit2;

    private long permissionBit3;

    public String nickName;

    /** 0=enabled; 1=disabled; 2=apply to join org; 3=reject to join org; */
    public byte status;

    public JwtPrincipal buildJwtPrincipal() {
        return SimpleJwtPrincipal.of(
                account.username, permissionBit1, permissionBit2, permissionBit3,
                nickName, status, null, false, tenent.vipLevel);
    }
}
