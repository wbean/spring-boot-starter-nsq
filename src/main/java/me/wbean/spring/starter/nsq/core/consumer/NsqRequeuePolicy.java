package me.wbean.spring.starter.nsq.core.consumer;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQMessage;

/**
 * Copyright ©2012-2018 Youzan.com All rights reserved
 * com.youzan.boot.autoconfigure.nsq.core.consumer
 *
 *
 *
 * Created by wbean
 * Created at 2018/2/2 上午9:58
 * Version 1.0
 * Change Log
 */
public interface NsqRequeuePolicy {

  boolean requeue(NSQConsumer consumer, NSQMessage nsqMessage, Throwable throwable);
}
