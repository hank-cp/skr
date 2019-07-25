package org.skr.registry;

import org.skr.security.PermissionDetail;

public interface PermissionRegistry extends PermissionDetail, Registry {

    RealmRegistry getRealm();

}
