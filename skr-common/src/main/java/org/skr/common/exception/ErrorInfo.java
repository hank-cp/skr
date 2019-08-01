package org.skr.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rits.cloning.Cloner;
import lombok.Getter;
import org.skr.common.util.Checker;
import org.skr.config.json.StringValuedEnum;

import java.util.Objects;

public interface ErrorInfo {

    enum ErrorLevel implements StringValuedEnum {
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

    ErrorInfo setMsg(String msg, String... args);

    ErrorInfo setPath(String path);

    ErrorInfo setLevel(ErrorLevel level);

    ErrorInfo setExceptionDetail(String exceptionDetail);

    ErrorInfo setFailedRpc(String failedRpc);

    @JsonProperty("ec")
    int getCode();

    @JsonProperty("msg")
    String getMsg();

    @JsonProperty("ep")
    String getPath();

    @JsonProperty("elv")
    ErrorLevel getLevel();

    @JsonProperty("ed")
    String getExceptionDetail();

    @JsonProperty("rpc")
    String getFailedRpc();

    //*************************************************************************
    // Implementation
    //*************************************************************************

    @Getter
    class ErrorInfoImpl implements ErrorInfo {

        public int code;
        public String path;
        public ErrorLevel level;
        public String msg;
        public String failedRpc;
        public String exceptionDetail;

        public ErrorInfoImpl(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @SuppressWarnings("ConfusingArgumentToVarargsMethod")
        public ErrorInfo setMsg(String msg, String... args) {
            ErrorInfoImpl newInstance = Cloner.shared().shallowClone(this);
            newInstance.msg = !Checker.isEmpty(args) ? String.format(msg, args) : msg;
            return newInstance;
        }

        public ErrorInfo setPath(String path) {
            ErrorInfoImpl newInstance = Cloner.shared().shallowClone(this);
            newInstance.path = path;
            return newInstance;
        }

        public ErrorInfo setLevel(ErrorLevel level) {
            ErrorInfoImpl newInstance = Cloner.shared().shallowClone(this);
            newInstance.level = level;
            return newInstance;
        }

        public ErrorInfo setExceptionDetail(String exceptionDetail) {
            ErrorInfoImpl newInstance = Cloner.shared().shallowClone(this);
            newInstance.exceptionDetail = exceptionDetail;
            return newInstance;
        }

        public ErrorInfo setFailedRpc(String failedRpc) {
            ErrorInfoImpl newInstance = Cloner.shared().shallowClone(this);
            newInstance.failedRpc = failedRpc;
            return newInstance;
        }
    }

    //*************************************************************************
    // Common Errors Definition
    //*************************************************************************

    ErrorInfo OK = new ErrorInfoImpl(0, null);
    ErrorInfo INTERNAL_SERVER_ERROR    = new ErrorInfoImpl(1001, "Internal server error.");
    ErrorInfo ENTITY_NOT_FOUND         = new ErrorInfoImpl(1002, "Entity not found.").setLevel(ErrorLevel.ERROR);
    ErrorInfo DELETION_RESTRICTED      = new ErrorInfoImpl(1003, "Deletion restricted.").setLevel(ErrorLevel.ERROR);
    ErrorInfo INVALID_SUBMITTED_DATA   = new ErrorInfoImpl(1004, "Invalid submitted data.").setLevel(ErrorLevel.ERROR);
    ErrorInfo INVALID_SERVER_DATA      = new ErrorInfoImpl(1005, "Invalid server data.");
    ErrorInfo SAVE_DATA_FAILED         = new ErrorInfoImpl(1006, "Save data failed.");
    ErrorInfo DUPLICATED_ENTITY        = new ErrorInfoImpl(1007, "Duplicated entity.").setLevel(ErrorLevel.ERROR);
    ErrorInfo AUTHENTICATION_REQUIRED  = new ErrorInfoImpl(1008, "Authentication required.").setLevel(ErrorLevel.ERROR);
    ErrorInfo PERMISSION_DENIED        = new ErrorInfoImpl(1009, "Permission Denied.").setLevel(ErrorLevel.ERROR);
    ErrorInfo PERMISSION_LIMITED       = new ErrorInfoImpl(1010, "Vip Level is not satisfied.").setLevel(ErrorLevel.ERROR);
    ErrorInfo PERMISSION_NOT_FOUND     = new ErrorInfoImpl(1010, "Permission not found.");
    ErrorInfo REGISTRATION_ERROR       = new ErrorInfoImpl(1011, "Registration error.");
    ErrorInfo CLASS_NOT_FOUND          = new ErrorInfoImpl(1012, "Class not found.");

    ErrorInfo NOT_AUTHENTICATED            = new ErrorInfoImpl(1100, "Account is not authenticated.");
    ErrorInfo ACCESS_TOKEN_EXPIRED         = new ErrorInfoImpl(1101, "Access token is expired.");
    ErrorInfo ACCESS_TOKEN_BROKEN          = new ErrorInfoImpl(1102, "Access token is broken.");
    ErrorInfo ACCESS_TOKEN_NOT_PROVIDED    = new ErrorInfoImpl(1103, "Access token is provided.");
    ErrorInfo REFRESH_TOKEN_EXPIRED        = new ErrorInfoImpl(1104, "Refresh token is expired.");
    ErrorInfo REFRESH_TOKEN_BROKEN         = new ErrorInfoImpl(1105, "Refresh token is broken.");
    ErrorInfo NOT_SUPPORT_AUTH_PRINCIPAL   = new ErrorInfoImpl(1106, "Not support auth principal.");
}
