package me.wbean.spring.starter.nsq.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by wbean on 2017/8/18
 */
public class NsqConsumerStartException extends NestedRuntimeException {
    public NsqConsumerStartException(String msg) {
        super(msg);
    }

    public NsqConsumerStartException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
