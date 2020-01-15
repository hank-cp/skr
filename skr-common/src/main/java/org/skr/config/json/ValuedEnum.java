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
package org.skr.config.json;

import org.laxture.spring.util.ApplicationContextProvider;
import org.skr.common.exception.ConfException;
import org.skr.common.exception.ErrorInfo;
import org.skr.common.util.BeanUtil;
import org.skr.config.EnumLabelMessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Method;
import java.util.Objects;

import static org.skr.common.util.BeanUtil.PRIMITIVE_TYPES;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public interface ValuedEnum<V> {

    V value();

    default String enumLabel() {
        try {
            return ApplicationContextProvider.getBean(getClass(), EnumLabelMessageSource.class)
                    .getMessage(this.getClass().getSimpleName(),
                            null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            return this.getClass().getSimpleName();
        }
    }

    default String valueLabel() {
        try {
            return ApplicationContextProvider.getBean(getClass(), EnumLabelMessageSource.class)
                    .getMessage(this.getClass().getSimpleName()+"."+value().toString(),
                            null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            return this.getClass().getSimpleName();
        }
    }

    static <E extends ValuedEnum<V>, V> E parse(E[] values, V value, E defaultItem) {
        for (E item : values) {
            if (!Objects.equals(item.value(), value)) continue;
            return item;
        }
        return defaultItem;
    }

    static <E extends ValuedEnum<V>, V> E parse(Class<?> enumType, V value) {
        if (value == null) return null;

        Method parseMethod = BeanUtil.getMethod(enumType, "parse", value.getClass());
        if (parseMethod == null) {
            // try to get primitive version "parse" method
            parseMethod = BeanUtil.getMethod(enumType, "parse", PRIMITIVE_TYPES.get(value.getClass()));
        }
        if (parseMethod == null) {
            throw new ConfException(ErrorInfo.PARSE_METHOD_NOT_FOUND.msgArgs(
                    value.getClass().getSimpleName(), enumType.getSimpleName()));
        }

        try {
            //noinspection unchecked
            return (E) parseMethod.invoke(parseMethod, value);
        } catch (Exception e) {
            throw new IllegalStateException(e.getLocalizedMessage(), e);
        }
    }
}
