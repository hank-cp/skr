package org.skr.common.util;

import org.joda.time.DateTime;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public final class Checker {

    private Checker() {}

    //********************* Empty Object Validation ***************************

    public static boolean isZero(Long longNum) {
        return longNum == null || longNum == 0;
    }

    public static boolean isZero(Optional<Long> longNum) {
        return longNum == null || !longNum.isPresent() || longNum.get() == 0;
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
        return text == null || text.length() <= 0 || "null".equals(text);
    }

    public static boolean isEmpty(Optional<? extends CharSequence> text) {
        return !text.isPresent() || isEmpty(text.get());
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
        return !collection.isPresent() || collection.get().isEmpty();
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

    public static boolean isAfterOrEqual(DateTime dt1, DateTime dt2) {
        return dt1.isEqual(dt2) || dt1.isAfter(dt2);
    }

    public static boolean isBeforeOrEqual(DateTime dt1, DateTime dt2) {
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

}
