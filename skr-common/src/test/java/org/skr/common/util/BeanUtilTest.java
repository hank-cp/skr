package org.skr.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.skr.common.exception.ErrorInfo;
import org.skr.config.json.ValuedEnum;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
}