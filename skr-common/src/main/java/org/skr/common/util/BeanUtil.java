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
package org.skr.common.util;

import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SuppressWarnings("unchecked")
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
    public static <E> void copyFields(@NonNull E source,
                                      @NonNull E target,
                                      String... ignoreFields) {
        copyIncludeOrExcludeFields(source, target, false,
                ArrayUtils.addAll(ignoreFields, "id", "uid", "createdBy", "createdAt", "updatedBy", "updatedAt"));
    }

    /**
     * @see #copyFields(Object, Object, String...)
     *
     * @param specifiedFields fields to be copied
     */
    public static <E> void copySpecifiedFields(@NonNull E source,
                                               @NonNull E target,
                                               String... specifiedFields) {
        copyIncludeOrExcludeFields(source, target, true, specifiedFields);
    }

    /**
     * @see #copyFields(Object, Object, String...)
     *
     * @param fields including or excluding fields
     * @param isInclude
     */
    private static <E> void copyIncludeOrExcludeFields(@NonNull E source,
                                                       @NonNull E target,
                                                       boolean isInclude,
                                                       String... fields) {
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

    public static <T> T getFieldValue(@NotNull Object target,
                                      @NonNull String fieldName) {
        String[] fieldPath = fieldName.split("\\.");
        Object obj = target;
        int i=0;
        while (i<fieldPath.length) {
            if (obj == null) break;
            obj = getFieldValue(obj, obj.getClass(), fieldPath[i]);
            i++;
        }

        return (T) obj;
    }

    public static Class getFieldClass(@NonNull Object target,
                                      @NonNull String fieldName) {
        try {
            return target.getClass().getDeclaredField(fieldName).getType();
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getFieldValue(@NonNull Object target,
                                        @NonNull Class clazz,
                                        @NonNull String fieldName) {
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

    public static void setFieldValue(@NonNull Object target,
                                     @NonNull String fieldName,
                                     Object value) {
        setFieldValue(target, target.getClass(), fieldName, value);
    }

    private static void setFieldValue(@NonNull Object target,
                                      @NonNull Class clazz,
                                      @NonNull String fieldName,
                                      Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException nsfe) {
            if (clazz.getSuperclass() != null) {
                setFieldValue(target, clazz.getSuperclass(), fieldName, value);
            } else {
                throw new RuntimeException("Set field "+fieldName+" failed.", nsfe);
            }
        } catch (Exception e) {
            throw new RuntimeException("Set field "+fieldName+" failed.", e);
        }
    }

    public static <T extends Serializable> T deepClone(@NonNull T o) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(o);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
            return (T) o.getClass().cast(in.readObject());
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy Object "+o.getClass().getName(), e);
        }
    }

    public static Method getDeclaredMethod(@NonNull Class<?> clazz,
                                           @NonNull String methodName,
                                           Class... parameterTypes) {
        Method method;
        try {
            method = parameterTypes.length > 0
                    ? clazz.getDeclaredMethod(methodName, parameterTypes)
                    : clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
        if (method != null) method.setAccessible(true);
        return method;
    }

    public static Method getMethod(@NonNull Class<?> clazz,
                                   @NonNull String methodName,
                                   Class... parameterTypes) {
        Method method;
        try {
            method = parameterTypes.length > 0
                    ? clazz.getMethod(methodName, parameterTypes)
                    : clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
        if (method != null) method.setAccessible(true);
        return method;
    }

}
