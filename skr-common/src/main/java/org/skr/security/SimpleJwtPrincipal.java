package org.skr.security;

import lombok.*;

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

    @Getter @Setter
    private String serviceJwtToken;

    private Boolean robot;

    private int vipLevel;

    @Override
    public Boolean isRobot() {
        return robot;
    }
}
