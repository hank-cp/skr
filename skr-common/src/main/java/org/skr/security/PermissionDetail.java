package org.skr.security;

import org.apache.commons.lang3.NotImplementedException;
import org.skr.config.json.IntValuedEnum;

public interface PermissionDetail {

    enum PermissionResult implements IntValuedEnum {
        PERMISSION_GRANTED(0), PERMISSION_DENIED(1), PERMISSION_LIMITATION(2);

        private final int value;

        PermissionResult(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return value;
        }

        public static PermissionResult parse(int value) {
            for (PermissionResult item : PermissionResult.values()) {
                if (item.value() != value) continue;
                return item;
            }
            return PERMISSION_DENIED;
        }
    }

    String getCode();

    default PermissionResult checkAuthorization(JwtPrincipal principal) {
        throw new NotImplementedException("not implemented yet");
    }

}
