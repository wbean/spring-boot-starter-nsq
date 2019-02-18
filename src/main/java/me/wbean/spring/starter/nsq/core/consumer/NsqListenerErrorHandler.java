package me.wbean.spring.starter.nsq.core.consumer;


import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQMessage;

import me.wbean.spring.starter.nsq.exception.ListenerExecutionFailedException;

/**
 * Created by wbean on 2017/8/16
 */
public interface NsqListenerErrorHandler {

    /**
     * Handle the consume error.
     *
     * @param message the NsqMessage {@link NSQMessage}
     * @param exception the exception the listener threw, wrapped in a {@link ListenerExecutionFailedException}.
     * @param consumer the consumer {@link NSQConsumer}
     *
     * @return the return value is ignored unless the annoated method has a
     *
     */
    Object handleError(NSQMessage message, ListenerExecutionFailedException exception, NSQConsumer consumer);
}
