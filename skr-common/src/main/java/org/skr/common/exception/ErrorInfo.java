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
import org.skr.common.util.Checker;
import org.skr.config.json.StringValuedEnum;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="https://github.com/hank-cp">Hank CP</a>
 */
@ToString
public class ErrorInfo {

    public enum ErrorLevel implements StringValuedEnum {
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
            for (ErrorLevel item : ErrorLevel.values()) {
                if (!Objects.equals(item.value(), value)) continue;
                return item;
            }
            return FATAL;
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
        errorInfo.exception = BaseException.toString(ex);
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
        // TODO I18N msg
        return !Checker.isEmpty(args) ? String.format(msg, (Object[])args) : msg;
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

    public static final ErrorInfo INTERNAL_SERVER_ERROR     = ErrorInfo.of(1001, "%s",                                      ErrorLevel.FATAL);
    public static final ErrorInfo ENTITY_NOT_FOUND          = ErrorInfo.of(1002, "Entity %s(%s) not found.");
    public static final ErrorInfo DELETION_RESTRICTED       = ErrorInfo.of(1003, "Deletion %s(%s)restricted.");
    public static final ErrorInfo INVALID_SUBMITTED_DATA    = ErrorInfo.of(1004, "Invalid submitted data. %s");
    public static final ErrorInfo INVALID_SERVER_DATA       = ErrorInfo.of(1005, "Invalid server data. %s");
    public static final ErrorInfo SAVE_DATA_FAILED          = ErrorInfo.of(1006, "Save data failed.");
    public static final ErrorInfo DUPLICATED_ENTITY         = ErrorInfo.of(1007, "Duplicated entity %s(%s).");
    public static final ErrorInfo CLASS_NOT_FOUND           = ErrorInfo.of(1008, "Class %s not found.",                     ErrorLevel.FATAL);
    public static final ErrorInfo REQUIRED_PROPERTY_NOT_SET = ErrorInfo.of(1009, "Required property %s is not set.",        ErrorLevel.FATAL);
    public static final ErrorInfo INCOMPATIBLE_TYPE         = ErrorInfo.of(1010, "Except type %s but %s is given.",         ErrorLevel.FATAL);
    public static final ErrorInfo NOT_NULLABLE              = ErrorInfo.of(1011, "%s could not be null.",                   ErrorLevel.FATAL);
    public static final ErrorInfo ILLEGAL_DATA              = ErrorInfo.of(1012, "Illegal data %s.");
    public static final ErrorInfo CONSTRUCTOR_NOT_FOUND     = ErrorInfo.of(1013, "Required constructor is not found for class %s.");
    public static final ErrorInfo METHOD_NOT_FOUND          = ErrorInfo.of(1014, "Method %s is not found for class %s.",    ErrorLevel.FATAL);
    public static final ErrorInfo FIELD_NOT_FOUND           = ErrorInfo.of(1015, "Field %s is not found for class %s.",     ErrorLevel.FATAL);

    public static final ErrorInfo AUTHENTICATION_REQUIRED       = ErrorInfo.of(1100, "Authentication required.");
    public static final ErrorInfo NOT_AUTHENTICATED             = ErrorInfo.of(1101, "Account %s is not authenticated.");
    public static final ErrorInfo ACCESS_TOKEN_EXPIRED          = ErrorInfo.of(1102, "Access token is expired.");
    public static final ErrorInfo ACCESS_TOKEN_BROKEN           = ErrorInfo.of(1103, "Access token is broken.");
    public static final ErrorInfo ACCESS_TOKEN_NOT_PROVIDED     = ErrorInfo.of(1104, "Access token is not provided.");
    public static final ErrorInfo REFRESH_TOKEN_EXPIRED         = ErrorInfo.of(1105, "Refresh token is expired.");
    public static final ErrorInfo REFRESH_TOKEN_BROKEN          = ErrorInfo.of(1106, "Refresh token is broken.");
    public static final ErrorInfo PERMISSION_DENIED             = ErrorInfo.of(1107, "Permission Denied.");
    public static final ErrorInfo PERMISSION_LIMITED            = ErrorInfo.of(1108, "Vip Level is not satisfied.", ErrorLevel.FATAL);
    public static final ErrorInfo PERMISSION_NOT_FOUND          = ErrorInfo.of(1109, "Permission %s not found.",    ErrorLevel.FATAL);

    public static final ErrorInfo PERMISSION_REGISTERED         = ErrorInfo.of(1200, "Permission %s has been registered to realm %s",   ErrorLevel.FATAL);
    public static final ErrorInfo END_POINT_REGISTERED          = ErrorInfo.of(1201, "EndPoint %s has been registered to realm %s",     ErrorLevel.FATAL);
    public static final ErrorInfo PERMISSION_REVOKE_FAILED      = ErrorInfo.of(1202, "Permission %s is enabled in realm %s. You have to re-register this realm without this permission to disable it first.",   ErrorLevel.FATAL);
    public static final ErrorInfo END_POINT_REVOKE_FAILED       = ErrorInfo.of(1203, "EndPoint %s is enabled in realm %s. You have to re-register this realm without this permission to disable it first.",     ErrorLevel.FATAL);
}
