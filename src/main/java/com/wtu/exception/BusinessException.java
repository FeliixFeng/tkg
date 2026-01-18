package com.wtu.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }

}
