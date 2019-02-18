package me.wbean.spring.starter.nsq.core.consumer;


import org.springframework.util.Assert;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.callbacks.NSQMessageCallback;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;

import me.wbean.spring.starter.nsq.core.NsqProperties;

/**
 * this class create  {@link NSQConsumer}s
 *
 * Created by wbean on 2017/8/15
 */
public class NsqConsumerFactory {
    public final static  NsqConsumerFactory INSTANCE = new NsqConsumerFactory();
    private NsqConsumerFactory(){}

    public NSQConsumer createConsumer(NsqProperties nsqProperties, NSQMessageCallback messageHandler, String topic, String channel){
        Assert.notNull(nsqProperties,"can't create consumer with empty NsqProperties");
        Assert.notNull(messageHandler, "can't create consumer with empty MessageHandler");
        Assert.notNull(topic,"can't create consumer with empty topic");
        Assert.notNull(channel, "can't create consumer with empty channel");

        NSQLookup lookup = new DefaultNSQLookup();
        lookup.addLookupAddress(nsqProperties.getHost(), nsqProperties.getPort());

        return new NSQConsumer(lookup, topic, channel, messageHandler, nsqProperties.getNsqConfig());
    }
}
