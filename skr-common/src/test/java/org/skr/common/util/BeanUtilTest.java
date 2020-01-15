package org.skr.common.util;

import org.junit.Test;
import org.skr.common.exception.ErrorInfo;
import org.skr.config.json.ValuedEnum;

import java.util.ArrayList;

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
}