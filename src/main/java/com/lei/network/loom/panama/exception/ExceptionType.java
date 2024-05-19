package com.lei.network.loom.panama.exception;

/**
 * <p>
 * ExceptionType
 * </p>
 *
 * @author 伍磊
 */
public enum ExceptionType {
    // CONTEXT层的异常，用于表示上下文中产生的一些通用错误
    CONTEXT,
    // NATIVE层的访问异常，用于表示在从Java中调用C语言动态库时出现的错误
    NATIVE,
    // NETWORK层的异常，用于表示网络框架处理读写请求时产生的错误
    NETWORK,
}
