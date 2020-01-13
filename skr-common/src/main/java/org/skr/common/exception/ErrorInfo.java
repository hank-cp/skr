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
package org.skr.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rits.cloning.Cloner;
import lombok.NonNull;
import lombok.ToString;
import org.laxture.spring.util.ApplicationContextProvider;
import org.skr.common.util.Checker;
import org.skr.config.ErrorMessageSource;
import org.skr.config.json.ValuedEnum;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@ToString
public class ErrorInfo {

    public enum ErrorLevel implements ValuedEnum<String> {
        WARNING("warn"), // client should prompt user with warning message
        ERROR("error"),  // client should stop user operating, generally caused by error input or data restrictions
        FATAL("fatal");  // client should stop to be functional, shit happens...

        private final String value;

        ErrorLevel(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        public static ErrorLevel parse(String value) {
            return ValuedEnum.parse(ErrorLevel.values(), value, FATAL);
        }
    }

    @NotNull
    private int code;
    @NotNull
    private String msg;
    private ErrorLevel level = ErrorLevel.ERROR;

    private Object[] args;
    private String exception;
    private Map<String, Object> extra;

    private boolean shared = true;
    private ClassLoader classLoader;

    public static ErrorInfo of(int code, String msg) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.code = code;
        errorInfo.msg = msg;
        return errorInfo;
    }

    public static ErrorInfo of(int code, String msg, ErrorLevel level) {
        ErrorInfo errorInfo = of(code, msg);
        errorInfo.level = level;
        return errorInfo;
    }

    public static ErrorInfo of(int code, String msg, ErrorLevel level, ClassLoader classLoader) {
        ErrorInfo errorInfo = of(code, msg, level);
        errorInfo.classLoader = classLoader;
        return errorInfo;
    }

    private static ErrorInfo getOrCopy(@NonNull ErrorInfo target) {
        ErrorInfo errorInfo;
        if (target.shared) {
            errorInfo = Cloner.shared().shallowClone(target);
            errorInfo.shared = false;
        } else {
            errorInfo = target;
        }
        return errorInfo;
    }

    public ErrorInfo exception(Throwable ex) {
        ErrorInfo errorInfo = getOrCopy(this);
        errorInfo.exception = ex.getLocalizedMessage();
        return errorInfo;
    }

    public ErrorInfo msgArgs(Object... args) {
        ErrorInfo errorInfo = getOrCopy(this);
        errorInfo.args = args;
        return errorInfo;
    }

    public ErrorInfo extra(String key, Object value) {
        ErrorInfo errorInfo = getOrCopy(this);
        if (errorInfo.extra == null) {
            errorInfo.extra = new HashMap<>();
        }
        errorInfo.extra.put(key, value);
        return errorInfo;
    }

    @JsonProperty("ec")
    public int getCode() {
        return code;
    }

    @JsonProperty("msg")
    public String getMsg() {
        ClassLoader classLoader = Optional.ofNullable(this.classLoader).orElse(getClass().getClassLoader());
        try {
            return ApplicationContextProvider.getBean(classLoader, ErrorMessageSource.class)
                    .getMessage(msg, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            return msg;
        }
    }

    @JsonProperty("elv")
    public ErrorLevel getLevel() {
        return level;
    }

    @JsonProperty("ex")
    public String getException() {
        return exception;
    }

    @JsonProperty("extra")
    public Map<String, Object> getExtra() {
        return extra;
    }

    public <T> T getExtra(String key) {
        if (Checker.isEmpty(extra)) return null;
        //noinspection unchecked
        return (T) extra.get(key);
    }

    public static ErrorInfo.ErrorLevel worstErrorLevel(List<ErrorInfo> errors) {
        if (Checker.isEmpty(errors)) return null;

        return errors.stream().map(ErrorInfo::getLevel)
                .filter(level -> level == ErrorInfo.ErrorLevel.FATAL)
                .findAny()
                .orElse(errors.stream().map(ErrorInfo::getLevel)
                        .filter(level -> level == ErrorInfo.ErrorLevel.ERROR)
                        .findAny().orElse(ErrorInfo.ErrorLevel.WARNING));
    }

    //*************************************************************************
    // Common Errors Definition
    //*************************************************************************

    public static final ErrorInfo OK                        = ErrorInfo.of(0, null);

    public static final ErrorInfo INTERNAL_SERVER_ERROR     = ErrorInfo.of(1001, "error.INTERNAL_SERVER_ERROR",    ErrorLevel.FATAL);
    public static final ErrorInfo ENTITY_NOT_FOUND          = ErrorInfo.of(1002, "error.ENTITY_NOT_FOUND");
    public static final ErrorInfo DELETION_RESTRICTED       = ErrorInfo.of(1003, "error.DELETION_RESTRICTED");
    public static final ErrorInfo INVALID_SUBMITTED_DATA    = ErrorInfo.of(1004, "error.INVALID_SUBMITTED_DATA");
    public static final ErrorInfo INVALID_SERVER_DATA       = ErrorInfo.of(1005, "error.INVALID_SERVER_DATA");
    public static final ErrorInfo SAVE_DATA_FAILED          = ErrorInfo.of(1006, "error.SAVE_DATA_FAILED");
    public static final ErrorInfo DUPLICATED_ENTITY         = ErrorInfo.of(1007, "error.DUPLICATED_ENTITY");
    public static final ErrorInfo INCOMPATIBLE_TYPE         = ErrorInfo.of(1010, "error.INCOMPATIBLE_TYPE",         ErrorLevel.FATAL);
    public static final ErrorInfo METHOD_NOT_FOUND          = ErrorInfo.of(1014, "error.METHOD_NOT_FOUND",          ErrorLevel.FATAL);
    public static final ErrorInfo FIELD_NOT_FOUND           = ErrorInfo.of(1015, "error.FIELD_NOT_FOUND",           ErrorLevel.FATAL);
    public static final ErrorInfo MISSING_PROPERTY          = ErrorInfo.of(1009, "error.MISSING_PROPERTY",          ErrorLevel.FATAL);
    public static final ErrorInfo MISSING_ARGUMENT          = ErrorInfo.of(1016, "error.MISSING_ARGUMENT",          ErrorLevel.FATAL);
    public static final ErrorInfo PARSE_METHOD_NOT_FOUND    = ErrorInfo.of(1017, "error.PARSE_METHOD_NOT_FOUND",    ErrorLevel.FATAL);

    public static final ErrorInfo AUTHENTICATION_REQUIRED       = ErrorInfo.of(1100, "error.AUTHENTICATION_REQUIRED");
    public static final ErrorInfo BAD_CERTIFICATION             = ErrorInfo.of(1101, "error.BAD_CERTIFICATION");
    public static final ErrorInfo ACCESS_TOKEN_EXPIRED          = ErrorInfo.of(1102, "error.ACCESS_TOKEN_EXPIRED");
    public static final ErrorInfo ACCESS_TOKEN_BROKEN           = ErrorInfo.of(1103, "error.ACCESS_TOKEN_BROKEN");
    public static final ErrorInfo ACCESS_TOKEN_NOT_PROVIDED     = ErrorInfo.of(1104, "error.ACCESS_TOKEN_NOT_PROVIDED");
    public static final ErrorInfo REFRESH_TOKEN_EXPIRED         = ErrorInfo.of(1105, "error.REFRESH_TOKEN_EXPIRED");
    public static final ErrorInfo REFRESH_TOKEN_BROKEN          = ErrorInfo.of(1106, "error.REFRESH_TOKEN_BROKEN");
    public static final ErrorInfo PERMISSION_DENIED             = ErrorInfo.of(1107, "error.PERMISSION_DENIED");
    public static final ErrorInfo PERMISSION_LIMITED            = ErrorInfo.of(1108, "error.PERMISSION_LIMITED",        ErrorLevel.FATAL);
    public static final ErrorInfo PERMISSION_NOT_FOUND          = ErrorInfo.of(1109, "error.PERMISSION_NOT_FOUND",      ErrorLevel.FATAL);
    public static final ErrorInfo CERTIFICATION_NOT_FOUND       = ErrorInfo.of(1110, "error.CERTIFICATION_NOT_FOUND");
    public static final ErrorInfo CERTIFICATION_REGISTERED      = ErrorInfo.of(1111, "error.CERTIFICATION_REGISTERED");
    public static final ErrorInfo LAST_CERTIFICATION            = ErrorInfo.of(1112, "error.LAST_CERTIFICATION");

    public static final ErrorInfo REALM_NOT_REGISTERED          = ErrorInfo.of(1200, "error.REALM_NOT_REGISTERED",          ErrorLevel.FATAL);
    public static final ErrorInfo REGISTER_REGISTRY_FAILED      = ErrorInfo.of(1202, "error.REGISTER_REGISTRY_FAILED",     ErrorLevel.FATAL);
    public static final ErrorInfo UNREGISTER_REGISTRY_FAILED    = ErrorInfo.of(1203, "error.UNREGISTER_REGISTRY_FAILED",    ErrorLevel.FATAL);
    public static final ErrorInfo UNINSTALL_REGISTRY_FAILED     = ErrorInfo.of(1204, "error.UNINSTALL_REGISTRY_FAILED",     ErrorLevel.FATAL);
}
