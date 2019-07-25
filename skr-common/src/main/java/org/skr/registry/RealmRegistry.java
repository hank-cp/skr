package org.skr.registry;

import javax.validation.constraints.NotNull;

public interface RealmRegistry extends Registry {

    @NotNull
    String getCode();

    @NotNull
    String getName();

}
