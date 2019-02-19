package test; /**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean
 */

import org.springframework.stereotype.Component;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQMessage;

import me.wbean.spring.starter.nsq.core.consumer.NsqRequeuePolicy;

/**
 * @author wbean
 * @date 2019/2/19 上午11:12
 */
@Component
class MyRequeuePolicy implements NsqRequeuePolicy {

    @Override
    public boolean requeue(NSQConsumer consumer, NSQMessage nsqMessage, Throwable throwable) {
        if (throwable instanceof RuntimeException && nsqMessage.getAttempts() < 10) {
            nsqMessage.requeue(1000);
        } else {
            nsqMessage.requeue(2000);
        }
        return false;
    }
}
