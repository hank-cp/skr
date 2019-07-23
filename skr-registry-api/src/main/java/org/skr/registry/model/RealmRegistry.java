package org.skr.registry.model;

import javax.validation.constraints.NotNull;

public interface RealmRegistry extends Registry {

    @NotNull
    String getCode();

    @NotNull
    String getName();

}
