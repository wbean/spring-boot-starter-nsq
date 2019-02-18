package me.wbean.spring.starter.nsq.core.consumer;


import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import com.github.brainlag.nsq.NSQConsumer;

import me.wbean.spring.starter.nsq.core.NsqProperties;
import me.wbean.spring.starter.nsq.exception.NsqConsumerStartException;

/**
 * the major class that control the lifecycle of consumer
 *
 * consumer start when Spring container start
 *
 *
 * Created by wbean on 2017/8/15
 */
public class NsqMessageListenerContainer implements SmartLifecycle {
    private DefaultMessageHandler messageHandler;
    private NsqProperties nsqProperties;
    private NSQConsumer consumer;

    private final Object lifecycleMonitor = new Object();

    private volatile boolean running;

    private int phase = 0;

    public NsqMessageListenerContainer(MethodNsqListenerEndpoint endpoint) {
        Assert.notNull(endpoint, "can't construct NsqMessageListenerContainer whit empty MethodNsqListenerEndpoint");

        this.nsqProperties = endpoint.getNsqProperties();
        this.nsqProperties.setAutoFinish(endpoint.isAutoFinish());
        if(endpoint.getRdy() > 0){
            this.nsqProperties.setMaxInFlight(endpoint.getRdy());
        }

        this.messageHandler = initMessageHandler(endpoint);

        this.consumer = NsqConsumerFactory.INSTANCE.createConsumer(nsqProperties, messageHandler, endpoint.getTopic(), endpoint.getChannel());
    }

    private DefaultMessageHandler initMessageHandler(MethodNsqListenerEndpoint endpoint) {
        DefaultMessageHandler messageHandler = new DefaultMessageHandler(endpoint.getBean(), endpoint.getMethod());
        messageHandler.setConsumer(this.consumer);
        messageHandler.setNsqMessageDecoder(endpoint.getNsqMessageDecoder());
        messageHandler.setNsqListenerErrorHandler(endpoint.getNsqListenerErrorHandler());
        messageHandler.setAutoFinish(endpoint.isAutoFinish());
        if(endpoint.getNsqRequeuePolicy() != null){
            messageHandler.setNsqRequeuePolicy(endpoint.getNsqRequeuePolicy());
        }else {
            messageHandler.setNsqRequeuePolicy(DefaultNsqRequeuePolicy.INSTANCE);
        }
        return messageHandler;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        synchronized (this.lifecycleMonitor){
            doStop(runnable);
        }
    }

    @Override
    public void start() {
        synchronized (this.lifecycleMonitor){
            doStart();
        }
        this.running = true;
    }

    public void doStart(){
        if(isRunning()){
            return;
        }

        Assert.notNull(consumer,"Consumer must not empty");

        try {
            consumer.start();
        } catch (Exception e) {
            throw new NsqConsumerStartException("nsq consumer start failed", e);
        }
    }


    @Override
    public void stop() {
        stop(() -> {});
    }

    private void doStop(Runnable runnable){
        if(isRunning()){
            this.running = false;
            consumer.shutdown();
        }
    }


    @Override
    public boolean isRunning() {
        return this.running;
    }


    @Override
    public int getPhase() {
        return this.phase;
    }
}
