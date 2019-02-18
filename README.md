# spring-boot-starter-nsq

## Artifcat
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-nsq</artifactId>
    <version>1.0-RELEASE</version>
</dependency>
```

## Example usage:

Example usage:
```java
public class NsqTest {
    
    @Resource
    private NsqTemplate nsqTemplate;
    
    
    class Order{
        private int id;
        private Long amount;
        private String goodsName;
    }
    
    //****** Consumer usage
    
    /**
     * common usage, auto ack
     * default decoder JSON.parseObject
     * default string charset UTF-8
     *
     * @param order
     */
    @NsqListener(topic="topic_name", channel="channel_name")
    public void onOrderCreated1(Order order){
        System.out.println(order.goodsName);
    }
    
    
    
    /**
     * common usage, auto ack
     * default string charset UTF-8
     *
     * @param message
     */
    @NsqListener(topic="topic_name", channel="channel_name")
    public void onOrderCreated2(String message){
        System.out.println(JSON.parseObject(new String(message), Order.class).goodsName);
    }
    
    
    
    /**
     * common usage, auto ack
     *
     * @param nsqMessage
     */
    @NsqListener(topic="topic_name", channel="channel_name")
    public void onOrderCreated2(NSQMessage nsqMessage){
        System.out.println(JSON.parseObject(new String(nsqMessage.getMessage()), Order.class).goodsName);
    }
    
    
    
    /**
     * common usage, manual ack
     *
     * @param order
     * @param nsqMessage
     */
    @NsqListener(topic="topic_name", channel="channel_name", autoFinish = false)
    public void onOrderCreated3(Order order, NSQMessage nsqMessage){
        System.out.println(order.goodsName);
        nsqMessage.finished();
    }
    
    
    
    @Component
    class MyMessageDecoder implements NsqMessageDecoder<Order>{
        @Override
        public Order decode(NSQMessage nsqMessage) {
            return JSON.parseObject(new String(nsqMessage.getMessage(), Charset.forName("GBK")), Order.class);
        }
    }
    
    /**
     * with user define message decoder
     *
     * @param order
     */
    @NsqListener(topic="topic_name", channel="channel_name", messageDecoder = "myMessageDecoder")
    public void onOrderCreated4(Order order){
        System.out.println(order.goodsName);
    }
    
    /**
     * throw any Exception to requeue with default requeue policy
     *
     * @param order
     */
    @NsqListener(topic="topic_name", channel="channel_name")
    public void onOrderCreated5(Order order){
        System.out.println(order.goodsName);
        throw new RuntimeException("requeue message use default policy");
    }
    
    
    
    @Component
    class MyRequeuePolicy implements NsqRequeuePolicy{
        @Override
        public boolean requeue(NSQConsumer consumer, NSQMessage nsqMessage, Throwable throwable) {
            if(throwable instanceof RuntimeException && nsqMessage.getAttempts() < 10){
                nsqMessage.requeue(1000);
            }else {
                nsqMessage.requeue(2000);
            }
            return false;
        }
    }
    /**
     * throw any Exception to requeue with user define requeue policy
     *
     * @param order
     */
    @NsqListener(topic="topic_name", channel="channel_name", requeuePolicy = "myRequeuePolicy")
    public void onOrderCreated6(Order order){
        System.out.println(order.goodsName);
        throw new RuntimeException("requeue message use user define policy");
    }
    
    
    //****** Producer usage
    
  
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
```

