package test; /**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean
 */

import java.util.concurrent.TimeoutException;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.github.brainlag.nsq.NSQMessage;
import com.github.brainlag.nsq.exceptions.NSQException;

import me.wbean.spring.starter.nsq.core.consumer.NsqListener;
import me.wbean.spring.starter.nsq.core.producer.NsqTemplate;

/**
 *
 * @author wbean
 * @date 2019/2/18 下午7:28
 */
@Component
public class NsqTest {
    @Resource
    private NsqTemplate nsqTemplate;


    public static class Order{
        private int id;
        private Long amount;
        private String goodsName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Long getAmount() {
            return amount;
        }

        public void setAmount(Long amount) {
            this.amount = amount;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }
    }

    /**
     * common usage, auto ack
     * default decoder JSON.parseObject
     * default string charset UTF-8
     *
     * @param order
     */
    @NsqListener(topic="topic_name", channel="channel_name")
    public void onOrderCreated1(Order order){
        System.out.println(order.getGoodsName());
    }

    /**
     * common usage, auto ack
     * default string charset UTF-8
     *
     * @param message
     */
    @NsqListener(topic="topic_name2", channel="channel_name")
    public void onOrderCreated2(String message){
        System.out.println(JSON.parseObject(new String(message), Order.class).getGoodsName());
    }

    /**
     * common usage, auto ack
     *
     * @param nsqMessage
     */
    @NsqListener(topic="topic_name3", channel="channel_name")
    public void onOrderCreated3(NSQMessage nsqMessage){
        System.out.println(JSON.parseObject(new String(nsqMessage.getMessage()), Order.class).getGoodsName());
    }

    /**
     * common usage, manual ack
     *
     * @param order
     * @param nsqMessage
     */
    @NsqListener(topic="topic_name4", channel="channel_name", autoFinish = false)
    public void onOrderCreated4(Order order, NSQMessage nsqMessage){
        System.out.println(order.getGoodsName());
        nsqMessage.finished();
    }

    /**
     * with user define message decoder
     *
     * @param order
     */
    @NsqListener(topic="topic_name5", channel="channel_name", messageDecoder = "myMessageDecoder")
    public void onOrderCreated5(Order order){
        System.out.println(order.getGoodsName());
    }

    /**
     * throw any Exception to requeue with default requeue policy
     * @see me.wbean.spring.starter.nsq.core.consumer.DefaultNsqRequeuePolicy
     *
     * @param order
     */
    @NsqListener(topic="topic_name6", channel="channel_name")
    public void onOrderCreated6(Order order){
        System.out.println(order.getGoodsName());
        throw new RuntimeException("requeue message use default policy");
    }

    /**
     * throw any Exception to requeue with user define requeue policy
     * @see me.wbean.spring.starter.nsq.core.consumer.NsqRequeuePolicy
     *
     * @param order
     */
    @NsqListener(topic="topic_name7", channel="channel_name", requeuePolicy = "myRequeuePolicy")
    public void onOrderCreated7(Order order){
        System.out.println(order.getGoodsName());
        throw new RuntimeException("requeue message use user define policy");
    }

    /**
     * common usage
     *
     * @param order
     * @throws TimeoutException
     * @throws NSQException
     */
    public void sendMessageTest1(Order order) throws TimeoutException, NSQException {
        nsqTemplate.send("topic_name", JSON.toJSONString(order));
    }

    /**
     * send message with JavaNsqClient SDK
     * @see com.github.brainlag.nsq.NSQProducer
     *
     * @param order
     */
    public void sendMessageTest2(Order order){
        nsqTemplate.execute(producer -> {
            try {
                producer.produce("topic_name", JSON.toJSONBytes(order));
            } catch (NSQException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return true;
        });
    }
}
