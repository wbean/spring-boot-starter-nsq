package me.wbean.spring.starter.nsq.exception;

/**
 * Created by wbean on 2017/8/18
 */
public class ListenerExecutionBusinessFailedException extends ListenerExecutionFailedException {
    public ListenerExecutionBusinessFailedException(String msg) {
        super(msg);
    }

    public ListenerExecutionBusinessFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
