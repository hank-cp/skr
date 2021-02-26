package org.skr.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.skr.common.exception.ErrorInfo;
import org.skr.config.json.ValuedEnum;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
public class BeanUtilTest {

    @Test
    public void callMethod() {
        ValuedEnum<?>[] values = BeanUtil.callMethod(
                ErrorInfo.ErrorLevel.class,
                ErrorInfo.ErrorLevel.class,
                "values");
        assertThat(values, arrayWithSize(3));

        String testStr = "asdf";
        String newStr = BeanUtil.callMethod(testStr, "replaceAll", "a", "b");
        assertThat(newStr, equalTo("bsdf"));
    }

    @Test
    public void testIsPrimitive() {
        Double d1 = 1.0;
        double d2 = 1.0;
        assertThat(BeanUtil.isPrimitive(d1), equalTo(true));
        assertThat(BeanUtil.isPrimitive(d2), equalTo(true));
        assertThat(BeanUtil.isPrimitive("string"), equalTo(true));
    }

    @Test
    public void testGetCollectionParameterizeType() {
        Method testMethod1 = Arrays.stream(getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals("testMethod1"))
                .findAny().orElse(null);
        Method testMethod2 = Arrays.stream(getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals("testMethod2"))
                .findAny().orElse(null);
        Method testMethod3 = Arrays.stream(getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals("testMethod3"))
                .findAny().orElse(null);
        assertThat(BeanUtil.getCollectionParameterizeType(testMethod1.getParameters()[0]), nullValue());
        assertThat(BeanUtil.getCollectionParameterizeType(testMethod2.getParameters()[0]), equalTo(String.class));
        assertThat(BeanUtil.getCollectionParameterizeType(testMethod3.getParameters()[0]), nullValue());
    }

    public void testMethod1(String param) {
        // do nothing
    }

    public void testMethod2(List<String> param) {
        // do nothing
    }

    public void testMethod3(List<?> param) {
        // do nothing
    }

    public static class A {
        static int s = -1;
        Map<String, String> map = new HashMap<>();
        B b = new B();
        C c = new C();
    }

    public static class B {
        Map<String, C> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<C> cList = new ArrayList<>();
    }

    public static class C {
        String d;
    }

    public static class D extends A {

    }

    @Test
    public void testGetFieldValue() {
        A a = new A();
        a.map.put("key", "value");
        a.b.list.add("li1");
        a.c.d = "c0";
        C c1 = new C();
        c1.d = "c1";
        a.b.cList.add(c1);
        C c2 = new C();
        c2.d = "c2";
        a.b.map.put("c2", c2);
        C c3 = new C();
        c3.d = "c3";
        a.b.map.put("c3", c3);

        assertThat(BeanUtil.getFieldValue(a, "c.d"), equalTo("c0"));
        assertThat(BeanUtil.getFieldValue(a, "map.*"), hasItem("value"));
        assertThat(BeanUtil.getFieldValue(a, "map.key"), equalTo("value"));
        assertThat(BeanUtil.getFieldValue(a, "b.list"), hasItem("li1"));
        assertThat(BeanUtil.getFieldValue(a, "b.cList.d"), hasItem("c1"));
        assertThat(BeanUtil.getFieldValue(a, "b.cList.*.d"), hasItem("c1"));
        assertThat(BeanUtil.getFieldValue(a, "b.map.*.d"), allOf(
                (Matcher) hasSize(2), hasItem("c2"), hasItem("c3")));
    }

    @Test
    public void testGetStaticValue() {
        assertThat(BeanUtil.getFieldValue(A.class, "s"), equalTo(-1));
        assertThat(BeanUtil.getFieldValue(D.class, "s"), equalTo(-1));
    }
}