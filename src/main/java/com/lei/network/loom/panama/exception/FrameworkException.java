package com.lei.network.loom.panama.exception;

/**
 * <p>
 * FrameworkException
 * </p>
 *
 * @author 伍磊
 */
public final class FrameworkException extends RuntimeException{

    public FrameworkException(ExceptionType exceptionType, String message) {
        this(exceptionType, message, null, (Object) null);
    }

    public FrameworkException(ExceptionType exceptionType, String message, Throwable throwable) {
        this(exceptionType, message, throwable, (Object) null);
    }

    public FrameworkException(ExceptionType exceptionType, String message, Object... args) {
        this(exceptionType, message, null, args);
    }

    public FrameworkException(ExceptionType exceptionType, String message, Throwable throwable, Object... args) {
        super(STR."Type: \{exceptionType}, Msg : \{args == null ? message : String.format(message, args)}", throwable);
    }
}
