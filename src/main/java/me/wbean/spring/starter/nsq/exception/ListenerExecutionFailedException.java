package me.wbean.spring.starter.nsq.exception;

/**
 * Created by wbean on 2017/8/16
 */
public class ListenerExecutionFailedException extends RuntimeException {
    public ListenerExecutionFailedException(String msg) {
        super(msg);
    }

    public ListenerExecutionFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
