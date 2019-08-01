package org.skr.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rits.cloning.Cloner;
import org.skr.common.util.Checker;
import org.skr.config.json.StringValuedEnum;

import java.util.Objects;

public class Errors {

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

    @JsonProperty("ec")
    public int code;

    @JsonProperty("ep")
    public String path;

    @JsonProperty("elv")
    public ErrorLevel level;

    @JsonProperty("msg")
    public String msg;

    @JsonProperty("rpc")
    public String failedRpc;

    @JsonProperty("ed")
    public String exceptionDetail;

    protected Errors() {}

    protected Errors(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Errors setMsg(String msg, String... args) {
        Errors newInstance = Cloner.shared().shallowClone(this);
        newInstance.msg = !Checker.isEmpty(args) ? String.format(msg, args) : msg;
        return newInstance;
    }

    public Errors setPath(String path) {
        Errors newInstance = Cloner.shared().shallowClone(this);
        newInstance.path = path;
        return newInstance;
    }

    public Errors setLevel(ErrorLevel level) {
        Errors newInstance = Cloner.shared().shallowClone(this);
        newInstance.level = level;
        return newInstance;
    }

    public Errors setExceptionDetail(String exceptionDetail) {
        Errors newInstance = Cloner.shared().shallowClone(this);
        newInstance.exceptionDetail = exceptionDetail;
        return newInstance;
    }

    //*************************************************************************
    // Common Errors Definition
    //*************************************************************************

    public static final Errors OK = new Errors(0, null);
    public static final Errors INTERNAL_SERVER_ERROR    = new Errors(1001, "Internal server error.");
    public static final Errors ENTITY_NOT_FOUND         = new Errors(1002, "Entity not found.").setLevel(ErrorLevel.ERROR);
    public static final Errors DELETION_RESTRICTED      = new Errors(1003, "Deletion restricted.").setLevel(ErrorLevel.ERROR);
    public static final Errors INVALID_SUBMITTED_DATA   = new Errors(1004, "Invalid submitted data.").setLevel(ErrorLevel.ERROR);
    public static final Errors INVALID_SERVER_DATA      = new Errors(1005, "Invalid server data.");
    public static final Errors SAVE_DATA_FAILED         = new Errors(1006, "Save data failed.");
    public static final Errors DUPLICATED_ENTITY        = new Errors(1007, "Duplicated entity.").setLevel(ErrorLevel.ERROR);
    public static final Errors AUTHENTICATION_REQUIRED  = new Errors(1008, "Authentication required.").setLevel(ErrorLevel.ERROR);
    public static final Errors PERMISSION_DENIED        = new Errors(1009, "Permission Denied.").setLevel(ErrorLevel.ERROR);
    public static final Errors PERMISSION_LIMITED       = new Errors(1010, "Vip Level is not satisfied.").setLevel(ErrorLevel.ERROR);
    public static final Errors PERMISSION_NOT_FOUND     = new Errors(1010, "Permission not found.");
    public static final Errors REGISTRATION_ERROR       = new Errors(1011, "Registration error.");
    public static final Errors CLASS_NOT_FOUND          = new Errors(1012, "Class not found.");

    public static final Errors NOT_AUTHENTICATED            = new Errors(1100, "Account is not authenticated.");
    public static final Errors ACCESS_TOKEN_EXPIRED         = new Errors(1101, "Access token is expired.");
    public static final Errors ACCESS_TOKEN_BROKEN          = new Errors(1102, "Access token is broken.");
    public static final Errors ACCESS_TOKEN_NOT_PROVIDED    = new Errors(1103, "Access token is provided.");
    public static final Errors REFRESH_TOKEN_EXPIRED        = new Errors(1104, "Refresh token is expired.");
    public static final Errors REFRESH_TOKEN_BROKEN         = new Errors(1105, "Refresh token is broken.");
    public static final Errors ACCOUNT_NOT_BELONG_TO_ORG    = new Errors(1106, "Account does not belong to org.");
    public static final Errors USER_DISABLED                = new Errors(1107, "User is disabled.");
    public static final Errors USER_NEED_APPROVAL           = new Errors(1108, "User need to be approved.");
    public static final Errors USER_REJECTED                = new Errors(1109, "User joining get rejected.");
    public static final Errors NOT_SUPPORT_AUTH_PRINCIPAL   = new Errors(1110, "Not support auth principal.");
}
