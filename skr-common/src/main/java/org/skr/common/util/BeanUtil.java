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
import java.lang.reflect.*;
import java.util.*;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SuppressWarnings("unchecked")
public class BeanUtil {

    public static Map<Class<?>, Class<?>> PRIMITIVE_TYPES = Map.of(
            Boolean.class,      boolean.class,
            Byte.class,         byte.class,
            Character.class,    char.class,
            Double.class,       double.class,
            Float.class,        float.class,
            Integer.class,      int.class,
            Long.class,         long.class,
            Short.class,        short.class,
            Void.class,         void.class
    );

    public static boolean isPrimitive(Object val) {
        if (val == null) return false;
        return isPrimitive(val.getClass());
    }

    public static boolean isPrimitive(@NonNull Class<?> clazz) {
        return PRIMITIVE_TYPES.containsKey(clazz)
                || PRIMITIVE_TYPES.containsValue(clazz)
                || String.class.isAssignableFrom(clazz);
    }

    public static Class<?> getCollectionParameterizeType(@NonNull Parameter parameter) {
        if (!Collection.class.isAssignableFrom(parameter.getType())) return null;
        if (!(parameter.getParameterizedType() instanceof ParameterizedType)) return null;
        Type parameterizedType = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
        return parameterizedType instanceof Class ? (Class<?>) parameterizedType : null;
    }

    /**
     * Copy Object <code>source</code> to Object <code>target</code>. This coping is
     * not recursive.
     * If a source field is {@link Collection}, it's element will be
     * {@link Collection#addAll(Collection)} to corresponding target field, instead of
     * copy the {@link Collection} reference. Hence if target's {@link Collection} is not
     * initialized and remains null, it won't be copied.
     */
    public static <E> void copyFields(@NonNull E source,
                                      @NonNull E target) {
        copyIncludeOrExcludeFields(source, target, false);
    }

    /**
     * @see #copyFields(Object, Object)
     *
     * @param fields to be excluded for copying
     */
    public static <E> void copyFieldsExcluding(@NonNull E source,
                                               @NonNull E target,
                                               String... fields) {
        copyIncludeOrExcludeFields(source, target, false, fields);
    }

    /**
     * @see #copyFields(Object, Object)
     *
     * @param fields to be included for copying
     */
    public static <E> void copyFieldsIncluding(@NonNull E source,
                                               @NonNull E target,
                                               String... fields) {
        copyIncludeOrExcludeFields(source, target, true, fields);
    }

    private static <E> void copyIncludeOrExcludeFields(@NonNull E source,
                                                       @NonNull E target,
                                                       boolean includeOrExclude,
                                                       String... fields) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        if (source == target) return;

        for (Field sourceField : source.getClass().getFields()) {
            try {
                if (Modifier.isStatic(sourceField.getModifiers())
                        || Modifier.isPrivate(sourceField.getModifiers())
                        || Modifier.isProtected(sourceField.getModifiers())
                        || (includeOrExclude && ArrayUtils.indexOf(fields, sourceField.getName()) < 0)
                        || (!includeOrExclude && ArrayUtils.indexOf(fields, sourceField.getName()) >= 0)) continue;

                Field targetField;
                try {
                    targetField = target.getClass().getField(sourceField.getName());
                } catch (NoSuchFieldException e) {
                    continue;
                }

                // deep copy collection
                if (Collection.class.isAssignableFrom(sourceField.getType())) {
                    Collection srcCollection = (Collection) sourceField.get(source);
                    Collection targetCollection;
                    try {
                        targetCollection = (Collection) targetField.get(target);
                    } catch (ClassCastException ex) {
                        continue;
                    }
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

                // deep copy map
                } else if (Map.class.isAssignableFrom(sourceField.getType())) {
                    Map srcMap = (Map) sourceField.get(source);
                    Map targetMap;
                    try {
                        targetMap = (Map) targetField.get(target);
                    } catch (ClassCastException ex) {
                        continue;
                    }
                    if (srcMap == null && targetMap == null) continue;
                    if (srcMap == null) {
                        targetMap.clear();
                    } else {
                        if (targetMap == null) {
                            targetField.set(target, srcMap);
                        } else {
                            // Overcome immutable collection
                            try {
                                targetMap.clear();
                                targetMap.putAll(srcMap);
                            } catch (UnsupportedOperationException e) {
                                targetField.set(target, srcMap);
                            }
                        }
                    }

                } else {
                    targetField.set(target, sourceField.get(source));
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "Copy field "+ sourceField.getName()+" failed.", e);
            }
        }
    }

    public static <T> T getFieldValue(@NotNull Object target,
                                      @NonNull String path) {
        String[] fieldPath = path.split("\\.");
        Object obj = target;
        int i=0;
        while (i<fieldPath.length) {
            if (obj == null) break;
            if ("*".equals(fieldPath[i])) {
                // merge map
                if (obj instanceof Map) {
                    obj = ((Map<?, ?>) obj).values();
                } else if (obj instanceof Collection) {
                    obj = obj;
                } else {
                    // non-support object fields
                    return null;
                }
            } else {
                if (obj instanceof Collection) {
                    List<Object> values = new ArrayList<>();
                    for (Object item : (Collection<?>) obj) {
                        Object value = getFieldValue(item, item.getClass(), fieldPath[i]);
                        values.add(value);
                    }
                    obj = values;
                } else {
                    obj = getFieldValue(obj, obj.getClass(), fieldPath[i]);
                }
            }
            i++;
        }
        return (T) obj;
    }

    public static Class<?> getFieldClass(@NonNull Object target,
                                      @NonNull String fieldName) {
        try {
            return target.getClass().getDeclaredField(fieldName).getType();
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getFieldValue(@NonNull Object target,
                                        @NonNull Class<?> clazz,
                                        @NonNull String fieldName) {
        if (Map.class.isAssignableFrom(clazz)) {
            return ((Map<?, ?>) target).get(fieldName);
        }

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
            Field field = clazz.getDeclaredField(fieldName);
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
                                           Class<?>... parameterTypes) {
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
                                   Class<?>... parameterTypes) {
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

    public static <R, O> R callMethod(O object,
                                      @NonNull String methodName,
                                      Object... parameters) {
        Class<O> clazz = (Class<O>) object.getClass();
        return callMethod(clazz, object, methodName, parameters);
    }

    /**
     * This method doesn't always function as expected. Be 100% sure
     * and tested when you use it.
     *
     * As known, this method is not worked for following case:
     * * parameter type is primitive number, e.g. int.class
     * * parameter type is general type, e.g. Object.class
     */
    public static <R> R callMethod(Class<?> clazz,
                                   Object object,
                                   @NonNull String methodName,
                                   Object... parameters) {
        if (object == null) return null;

        Method method;
        // try get method from `getMethod`
        if (Checker.isEmpty(parameters)) {
            method = getMethod(clazz, methodName);
        } else {
            method = getMethod(clazz, methodName,
                    Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new));
        }

        // try get method from `getDeclaredMethod`
        if (method == null) {
            if (Checker.isEmpty(parameters)) {
                method = getDeclaredMethod(clazz, methodName);
            } else {
                method = getDeclaredMethod(clazz, methodName,
                        Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new));
            }
        }

        if (method == null) return null;

        try {
            return (R) method.invoke(object, parameters);
        } catch (IllegalAccessException | InvocationTargetException ignore) {
        }
        return null;
    }

}
