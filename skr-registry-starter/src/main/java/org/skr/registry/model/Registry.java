package org.skr.registry.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Registry {

    String getType();

}
