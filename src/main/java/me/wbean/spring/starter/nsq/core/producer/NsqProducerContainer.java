package me.wbean.spring.starter.nsq.core.producer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.Assert;

import com.github.brainlag.nsq.NSQConfig;
import com.github.brainlag.nsq.NSQProducer;

import me.wbean.spring.starter.nsq.core.NsqProperties;
import me.wbean.spring.starter.nsq.exception.NsqProduceStartException;

/**
 * nsq producer container lifecycle start with Spring
 *
 * this class create a {@link NSQProducer} when its lifecycle start
 *
 * @see NSQProducer
 *
 * Created by wbean on 2017/8/10
 */
public class NsqProducerContainer implements SmartLifecycle {

    private static final Log log = LogFactory.getLog(NsqProducerContainer.class);

    private NsqProperties nsqProperties;

    /**
     *  Nsq 生产者
     */
    private NSQProducer producer;

    private volatile boolean running;

    public NsqProducerContainer(NsqProperties nsqProperties){
        this.nsqProperties = nsqProperties;
    }

    @Override
    public void start(){
        log.info("NsqProducerContainer start with properties " + nsqProperties);
        NSQConfig config = nsqProperties.getNsqConfig();

        Assert.notNull(config, "NsqProducerContainer can't start with empty config");

        //启动生产者
        producer = new NSQProducer();
        producer.setConfig(config);
        producer.addAddress(nsqProperties.getHost(), nsqProperties.getPort());
        try {
            producer.start();
        } catch (Exception e) {
            log.error("nsq producer start failed with config" + config, e);
            throw new NsqProduceStartException("nsq producer start failed", e);
        }
        this.running = true;
    }

    @Override
    public void stop() {
        log.info("NsqProducerContainer stop");
        if (producer != null) {
            producer.shutdown();
        }
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        stop();
        runnable.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    public NSQProducer getProducer() {
        return producer;
    }
}
