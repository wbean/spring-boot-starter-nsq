package me.wbean.spring.starter.nsq.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by wbean on 2017/8/15
 */
public class NsqProduceStartException extends NestedRuntimeException {
    public NsqProduceStartException(String msg) {
        super(msg);
    }

    public NsqProduceStartException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
