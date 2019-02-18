package me.wbean.spring.starter.nsq.core.consumer;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.brainlag.nsq.NSQConfig;
import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQMessage;

import me.wbean.spring.starter.nsq.core.NsqConstant;

/**
 * Copyright ©2012-2018 Youzan.com All rights reserved
 * com.youzan.boot.autoconfigure.nsq.core.consumer
 *
 * Created by wbean
 * Created at 2018/2/2 上午9:59
 * Version 1.0
 * Change Log
 */
public class DefaultNsqRequeuePolicy implements NsqRequeuePolicy {
    private static final Log log = LogFactory.getLog(DefaultMessageHandler.class);

    public static DefaultNsqRequeuePolicy INSTANCE = new DefaultNsqRequeuePolicy();

    /*消息重试间隔时间,单位为秒*/
    private int intervalInSecond = 120;

    /**
     * 最大延迟时间
     */
    private int MAX_REQUEUE_DELAY_SECOND = NsqConstant.MAX_REQUEUE_DELAY;

    @Override
    public boolean requeue(NSQConsumer consumer, NSQMessage nsqMessage, Throwable throwable) {
        int nextConsumingInSecond = (nsqMessage.getAttempts() + 1) * intervalInSecond;
        if (nextConsumingInSecond > MAX_REQUEUE_DELAY_SECOND) {
            nextConsumingInSecond = MAX_REQUEUE_DELAY_SECOND;
        }
        try {
            nsqMessage.requeue(new Long(NsqConstant.TIME_UNIT.toMillis(intervalInSecond)).intValue());
            log.info(String.format("message requeue in %s second, message=%s, attempts=%d", nextConsumingInSecond, new String(nsqMessage.getId()), nsqMessage.getAttempts()));
            return true;
        } catch (Exception e) {
            log.error(String.format("message requeue exception, message=%s, attempts=%d", new String(nsqMessage.getId()), nsqMessage.getAttempts()), e);
        }
        return false;
    }
}
