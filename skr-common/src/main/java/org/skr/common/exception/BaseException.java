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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The super class for exceptions
 */
public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 2846815275271113791L;

    public static final int TOP_STACK = 5;

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorInfo getErrorInfo() {
        return ErrorInfo.INTERNAL_SERVER_ERROR;
    }

    public String getSource() {
        StackTraceElement element = getInterestingStackTraceElement(this);
        if (element == null) return null;
        String fullClassName = element.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        return String.format("%s.%s(%s)", className, methodName, lineNumber);
    }

    public static StackTraceElement getInterestingStackTraceElement(Throwable cause) {
        for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
            if (stackTraceElement.getLineNumber() > 0) {
                return stackTraceElement;
            }
        }
        return null;
    }

    public static String summary(Throwable cause) {
        StackTraceElement element = getInterestingStackTraceElement(cause);
        if (element == null) return null;
        String fullClassName = element.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        return String.format("%s : %s.%s(%s) : %s", cause.getClass().getSimpleName(),
                className, methodName, lineNumber, cause.getLocalizedMessage());
    }

    public static List<StackTraceElement> getTopStackTraceElement(Throwable cause) {
        List<StackTraceElement> topStack = new ArrayList<>(TOP_STACK);
        for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
            if (stackTraceElement.getLineNumber() <= 0) continue;
            topStack.add(stackTraceElement);
            if (topStack.size() >= 5) break;
        }
        return topStack;
    }

    public static String summaryTopStack(Throwable cause) {
        List<StackTraceElement> stack = getTopStackTraceElement(cause);
        return stack.stream().map(element -> {
            String fullClassName = element.getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
            return String.format("%s:%s(%s)", className, element.getMethodName(), element.getLineNumber());
        }).collect(Collectors.joining("\n"));
    }

}
