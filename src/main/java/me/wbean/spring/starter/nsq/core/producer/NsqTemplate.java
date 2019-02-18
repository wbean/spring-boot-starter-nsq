package me.wbean.spring.starter.nsq.core.producer;



import java.util.concurrent.TimeoutException;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.github.brainlag.nsq.NSQMessage;
import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;

/**
 * this template class offer a callback interface {@link ProducerCallback}
 * to operate message whit origin {@link NSQProducer}
 *
 * also offer to simple method to send message with json
 *
 * @see NSQProducer
 * @see ProducerCallback
 *
 * Created by wbean on 2017/8/15
 */
public class NsqTemplate<T> {

    private static final Log log = LogFactory.getLog(NsqTemplate.class);

    @Resource
    NsqProducerContainer nsqProducerContainer;

    public NsqTemplate(){}

    public NsqTemplate(NsqProducerContainer nsqProducerContainer){
        Assert.notNull(nsqProducerContainer, "can't construct NsqTemplate with empty NsqProducerContainer");
        this.nsqProducerContainer = nsqProducerContainer;
    }

    /**
     * Execute some arbitrary operation(s) on the producer and return the result.
     *
     * @param callBack
     * @return
     */
    public T execute(ProducerCallback<T> callBack){
        return callBack.doSend(nsqProducerContainer.getProducer());
    }

    /**
     * send message to topic simplify
     *
     * @param topic
     * @param msg
     * @throws NSQException
     */
    public void send(String topic, String msg) throws TimeoutException, NSQException {
        //指令 内容都不能为空
        if (topic == null || msg == null) {
            log.warn("topic or message can not be null");
            return;
        }

        // 发送消息
        this.nsqProducerContainer.getProducer().produce(topic, msg.getBytes());
    }
}
