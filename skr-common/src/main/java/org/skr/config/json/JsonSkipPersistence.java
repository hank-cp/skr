package org.skr.config.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Combine with {@link com.fasterxml.jackson.databind.ObjectMapper} provided by
 * {@link org.skr.common.util.JsonUtil#setupPersistentObjectMapper}, annotated
 * field will be ignored in serialization.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSkipPersistence {

}
