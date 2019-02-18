package me.wbean.spring.starter.nsq.core.consumer;

import com.github.brainlag.nsq.NSQMessage;

/**
 * Created by wbean on 2018/1/18
 */
public interface NsqMessageDecoder<T> {
    /**
     * NSQ message decoder implement this function
     *
     * @param nsqMessage
     * @return
     */
  T decode(NSQMessage nsqMessage);
}
