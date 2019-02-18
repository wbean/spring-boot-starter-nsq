package me.wbean.spring.starter.nsq.core.producer;

import com.github.brainlag.nsq.NSQProducer;

/**
 * A callback for executing arbitrary operations on the {@link NSQProducer}.
 *
 * Created by wbean on 2017/8/15
 */
public interface ProducerCallback<T> {
    T doSend(NSQProducer producer);
}
