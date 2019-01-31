package org.skr.security;

import org.skr.common.Constants;

public interface PermissionDetail {

    String getCode();

    String getName();

    long getBit1();

    long getBit2();

    long getBit3();

    int getVipLevel();

    default int checkAuthorization(JwtPrincipal principal) {
        boolean granted = (principal.getPermissionBit1() & getBit1()) != 0
                && (principal.getPermissionBit2() & getBit2()) != 0
                && (principal.getPermissionBit3() & getBit3()) != 0;
        if (!granted) return Constants.PERMISSION_DENIED;

        if (principal.getVipLevel() < getVipLevel())
            return Constants.PERMISSION_NOT_PAID;

        return Constants.PERMISSION_GRANTED;
    }

}
