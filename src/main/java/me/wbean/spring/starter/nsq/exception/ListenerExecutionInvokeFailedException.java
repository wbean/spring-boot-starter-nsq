package me.wbean.spring.starter.nsq.exception;

/**
 * Created by wbean on 2017/8/18
 */
public class ListenerExecutionInvokeFailedException extends ListenerExecutionFailedException {
    public ListenerExecutionInvokeFailedException(String msg) {
        super(msg);
    }

    public ListenerExecutionInvokeFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
