package org.skr.common.util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class CollectionUtils {

    //*************************************************************************
    // pre-Java9 Collection Util
    //*************************************************************************

    public static <T> T[] array(T... args) {
        return args;
    }

    public static <T> List<T> list(T... args) {
        return Arrays.asList(args);
    }

    public static <T> Set<T> set(T... args) {
        Set<T> result = new HashSet<T>(args.length);
        result.addAll(Arrays.asList(args));
        return result;
    }

    public static <K, V> Map<K, V> map(Entry... entries) {
        Map<K, V> result = new LinkedHashMap<>(entries.length);

        for (Entry<? extends K, ? extends V> entry : entries) {
            if (entry != null) result.put(entry.key, entry.value);
        }

        return result;
    }

    public static <K, V> Entry<K, V> entry(K key, V value) {
        return new Entry<>(key, value);
    }

    public static <K, V> Entry<K, V> optionalEntry(K key, V value) {
        if (value == null) return null;
        return new Entry<>(key, value);
    }

    public static <K, V> Entry<K, V> solidEntry(K key, @NotNull V value) {
        if (value == null) throw new RuntimeException("Entry value must not be empty");
        return new Entry<>(key, value);
    }

    public static class Entry<K, V> {
        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    //*************************************************************************
    // Collections Utils
    //*************************************************************************

    public static <T> int indexOf(List<T> list, Function<T, Boolean> func) {
        if (Checker.isEmpty(list)) return -1;

        for (int i=0; i<list.size(); i++) {
            if (func.apply(list.get(i))) return i;
        }
        return -1;
    }

    public static <T> int lastIndexOf(List<T> list, Function<T, Boolean> func) {
        if (Checker.isEmpty(list)) return -1;

        int index = -1;
        for (int i=0; i<list.size(); i++) {
            if (func.apply(list.get(i))) index = i;
        }
        return index;
    }

    public static <T> boolean removeIf(Collection<T> collection, Predicate<T> func) {
        if (Checker.isEmpty(collection)) return false;

        Optional<T> obj = collection.stream().filter(func).findAny();
        return obj.isPresent() && collection.remove(obj.get());
    }

    public static <T> boolean replaceIf(List<T> list, T replacement, Predicate<T> func) {
        if (Checker.isEmpty(list)) return false;

        final boolean[] replaced = {false};
        list.replaceAll(item -> {
            if (func.test(item)) {
                replaced[0] = true;
                return replacement;
            } else {
                return item;
            }
        });
        return replaced[0];
    }

    public static <T> List<T> arrayToList(T[] array) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }

    public static List<Long> arrayToList(long[] array) {
        List<Long> list = new ArrayList<>();
        for (long o : array) {
            list.add(o);
        }
        return list;
    }

    public static <T> Map<String, T> covertToMap(ScriptObjectMirror som) {
        HashMap<String, T> map = new HashMap<>();
        for (String key : som.getOwnKeys(true)) {
            map.put(key, (T) som.get(key));
        }
        return map;
    }

    //*************************************************************************
    // Java8 Lambda Map Util
    //*************************************************************************

    private static <T> Map<String,T> buildMap(Map<String,T> _map, Function<Object,T>[] entries) {
        Map<String, T> map=_map;
        for( Function<Object,T> entry: entries ) {
            final Method m;
            try {
                m = entry.getClass().getDeclaredMethod("apply", Object.class);
            } catch (NoSuchMethodException nsme ) { throw new RuntimeException(nsme); }
            final Parameter p = m.getParameters()[0];
            final String key = p.getName();
            final T value = entry.apply(null);
            map.put(key,value);
        }
        return map;
    }

    public static <R> Map<String,R> hashMap(Function<Object, R>... entries) {
        return buildMap(new HashMap<>(), entries);
    }

    public static <R> Map<String,R> treeMap(Function<Object, R>... entries) {
        return buildMap(new TreeMap<>(), entries);
    }
}
