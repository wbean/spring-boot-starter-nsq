package test; /**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean
 */

import java.nio.charset.Charset;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.github.brainlag.nsq.NSQMessage;

import me.wbean.spring.starter.nsq.core.consumer.NsqMessageDecoder;

/**
 * @author wbean
 * @date 2019/2/19 上午11:11
 */
@Component
class MyMessageDecoder implements NsqMessageDecoder<NsqTest.Order> {
    @Override
    public NsqTest.Order decode(NSQMessage nsqMessage) {
        return JSON.parseObject(new String(nsqMessage.getMessage(), Charset.forName("GBK")), NsqTest.Order.class);
    }
}
