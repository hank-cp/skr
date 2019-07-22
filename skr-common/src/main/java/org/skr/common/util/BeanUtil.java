package org.skr.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

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
    @SuppressWarnings("unchecked")
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

}
