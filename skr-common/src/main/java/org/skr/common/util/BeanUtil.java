package org.skr.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.ArrayUtils;
import org.skr.config.ApplicationContextProvider;
import org.skr.config.json.*;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Stream;

public class BeanUtil {

    /**
     * Copy Object <code>source</code> to Object <code>target</code>. This coping is
     * not recursive.
     * If a source field is {@link Collection}, it's element will be
     * {@link Collection#addAll(Collection)} to corresponding target field, instead of
     * copy the {@link Collection} reference. Hence if target's {@link Collection} is not
     * initialized and remains null, it won't be copied.
     *
     * @param ignoreFields fields to be ignored
     */
    public static <E> void copyFields(E source, E target, String... ignoreFields) {
        copyIncludeOrExcludeFields(source, target, false,
                ArrayUtils.addAll(ignoreFields, "id", "uid", "createdBy", "createdAt", "updatedBy", "updatedAt"));
    }

    /**
     * @see #copyFields(Object, Object, String...)
     *
     * @param specifiedFields fields to be copied
     */
    public static <E> void copySpecifiedFields(E source, E target, String... specifiedFields) {
        copyIncludeOrExcludeFields(source, target, true, specifiedFields);
    }

    /**
     * @see #copyFields(Object, Object, String...)
     *
     * @param fields, including or excluding fields
     * @param isInclude
     */
    public static <E> void copyIncludeOrExcludeFields(E source, E target, boolean isInclude, String... fields) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        if (source == target) return;

        for (Field field : source.getClass().getFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())
                        || Modifier.isPrivate(field.getModifiers())
                        || Modifier.isProtected(field.getModifiers())
                        || (isInclude && ArrayUtils.indexOf(fields, field.getName()) < 0)
                        || (!isInclude && ArrayUtils.indexOf(fields, field.getName()) >= 0)) continue;

                Field targetField;
                try {
                    targetField = target.getClass().getField(field.getName());
                } catch (NoSuchFieldException e) {
                    continue;
                }

                if (Collection.class.isAssignableFrom(field.getType())) {
                    Collection srcCollection = (Collection) field.get(source);
                    Collection targetCollection = (Collection) targetField.get(target);
                    if (srcCollection == null && targetCollection == null) continue;
                    if (srcCollection == null) {
                        targetCollection.clear();
                    } else {
                        if (targetCollection == null) {
                            targetField.set(target, srcCollection);
                        } else {
                            // Overcome immutable collection
                            try {
                            targetCollection.clear();
                            targetCollection.addAll(srcCollection);
                            } catch (UnsupportedOperationException e) {
                                targetField.set(target, srcCollection);
                            }
                        }
                    }

                } else {
                    targetField.set(target, field.get(source));
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "Copy field "+ field.getName()+" failed.", e);
            }
        }
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = ApplicationContextProvider.getBean(ObjectMapper.class);
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            setupObjectMapper(objectMapper);
        }
        return objectMapper;
    }

    /** Additional Config for Jackson ObjectMapper */
    public static void setupObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addSerializer(IntValuedEnum.class, new IntValuedEnumSerializer());
        module.addSerializer(StringValuedEnum.class, new StringValuedEnumSerializer());
        module.addSerializer(Stream.class, new StreamSerializer());
        objectMapper.registerModule(module);

        objectMapper.setVisibility(objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY));
    }

    public static <T> T fromJSON(final Class type,
                                 final String json) {
        try {
            return getObjectMapper().readValue(json, (Class<T>) type);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static <T> T fromJSON(final TypeReference<T> type,
                                 final String json) {
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize json text failed. "+json, e);
        }
    }

    public static String toJSON(Object obj, Class<?> jsonViewClazz) {
        try {
            return getObjectMapper().writerWithView(jsonViewClazz).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static String toJSON(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialize object<"+obj.getClass().getName()+"> to json failed.", e);
        }
    }

    public static Class getFieldClass(Object target,String fieldName) {
        try {
            return target.getClass().getDeclaredField(fieldName).getType();
        } catch (Exception e) {
            throw new RuntimeException("Get Field class"+fieldName+" failed.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName) {
        return (T) getFieldValue(target, target.getClass(), fieldName);
    }

    private static Object getFieldValue(Object target, Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException nsfe) {
            if (clazz.getSuperclass() != null) {
                return getFieldValue(target, clazz.getSuperclass(), fieldName);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        setFieldValue(target, target.getClass(), fieldName, value);
    }

    private static void setFieldValue(Object target, Class clazz, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException nsfe) {
            if (clazz.getSuperclass() != null) {
                setFieldValue(target, clazz.getSuperclass(), fieldName, value);
            } else {
                throw new RuntimeException("Set Private Field "+fieldName+" failed.", nsfe);
            }
        } catch (Exception e) {
            throw new RuntimeException("Set Private Field "+fieldName+" failed.", e);
        }
    }

    public static Method getMethodSafe(Class<?> clazz, String methodName, Class... parameterTypes) {
        try {
            if (parameterTypes.length > 0) return clazz.getMethod(methodName, parameterTypes);
            else return clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
