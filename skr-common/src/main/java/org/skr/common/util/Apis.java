package org.skr.common.util;

import org.skr.common.Errors;
import org.springframework.data.domain.Page;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static org.skr.common.util.CollectionUtils.*;

public class Apis {

    public static Map<String, Object> apiResult(@NotNull Errors ret) {
        return map(
                entry("ret", ret.code),
                entry("msg", ret.msg),
                entry("elv", Optional.ofNullable(ret.level).map(Errors.ErrorLevel::value).orElse(null)),
                entry("ep", Optional.ofNullable(ret.path).orElse(null)),
                entry("st", System.currentTimeMillis())
        );
    }

    public static Map<String, Object> apiResult(@NotNull Errors ret, Map<String, Object> data) {
        Map<String, Object> map = apiResult(ret);
        if (data != null) {
            data.forEach(map::put);
        }
        return map;
    }

    public static Map<String, Object> apiResult(@NotNull Errors ret, Page pageData) {
        Map<String, Object> map = apiResult(ret);
        if (pageData != null) {
            map.put("data", map(
                    entry("total", pageData.getTotalPages()),
                    entry("page", pageData.getNumber()+1),
                    entry("records", pageData.getTotalElements()),
                    entry("rows", pageData.getContent())
            ));
        }
        return map;
    }

    public static Map<String, Object> apiResult(@NotNull Errors ret, List resultData) {
        Map<String, Object> map = apiResult(ret);
        if (resultData != null) {
            map.put("data", map(
                    entry("total", 1),
                    entry("page", 1),
                    entry("records", resultData.size()),
                    entry("rows", resultData)
            ));
        }
        return map;
    }

    public static <T> List<Errors> convertViolationsToErrors(Set<ConstraintViolation<T>> violations) {
        if (Checker.isEmpty(violations)) return new ArrayList<>();
        return violations.stream().map(
                violation -> Errors.INVALID_SUBMITTED_DATA
                        .setPath(violation.getPropertyPath().toString())
                        .setMsg(violation.getMessage())
                        .setLevel(Errors.ErrorLevel.ERROR)
        ).collect(Collectors.toList());
    }
}
