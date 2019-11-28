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

import org.skr.common.exception.ErrorInfo;

import javax.validation.ConstraintViolation;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class Checker {

    private Checker() {}

    //********************* Empty Object Validation ***************************

    public static boolean isZero(Long longNum) {
        return longNum == null || longNum == 0;
    }

    public static boolean isZero(Optional<Long> longNum) {
        return longNum == null || longNum.isEmpty() || longNum.get() == 0;
    }

    public static boolean isZero(Integer integer) {
        return integer == null || integer == 0;
    }

    public static boolean isZero(Double doubleNum) {
        return doubleNum == null || doubleNum == 0;
    }

    public static boolean isPositive(Integer integer) {
        return integer != 0 && integer > 0;
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() <= 0 || "null".contentEquals(text);
    }

    public static boolean isEmpty(Optional<? extends CharSequence> text) {
        return text.isEmpty() || isEmpty(text.get());
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isExistedFile(File file) {
        return file != null && file.exists();
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(File file) {
        return file == null || !file.exists() || file.length() <= 0;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmptyCollection(Optional<? extends Collection> collection) {
        return collection.isEmpty() || collection.get().isEmpty();
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Iterable iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Date date) {
        return date == null || date.getTime() == 0;
    }

    public static boolean isDeleted(Boolean deleted) {
        return deleted != null && deleted;
    }

    public static boolean isAfterOrEqual(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.isEqual(dt2) || dt1.isAfter(dt2);
    }

    public static boolean isBeforeOrEqual(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.isEqual(dt2) || dt1.isBefore(dt2);
    }

    public static boolean isNumber(String str) {
        return str.matches("\\d+");
    }

    public static boolean isTrue(Boolean bool) {
        return bool != null && bool;
    }

    public static boolean isTrue(Optional<Boolean> bool) {
        return bool != null && bool.isPresent() && bool.get();
    }

    public static <T> List<ErrorInfo> convertViolationsToErrorInfo(Set<ConstraintViolation<T>> violations) {
        if (Checker.isEmpty(violations)) return new ArrayList<>();
        return violations.stream().map(
                violation -> ErrorInfo.INVALID_SUBMITTED_DATA
                        .extra("path", violation.getPropertyPath().toString())
                        .msgArgs(violation.getMessage())
        ).collect(Collectors.toList());
    }
}
