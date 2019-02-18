package me.wbean.spring.starter.nsq.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import me.wbean.spring.starter.nsq.core.consumer.NsqListenerAnnotationBeanPostProcessor;
import me.wbean.spring.starter.nsq.core.consumer.NsqListenerEndpointRegister;
import me.wbean.spring.starter.nsq.core.producer.NsqProducerContainer;
import me.wbean.spring.starter.nsq.core.producer.NsqTemplate;

/**
 * class that register a {@link NsqProducerContainer} bean and a {@link NsqTemplate} bean for send message.
 * also register a {@link NsqListenerAnnotationBeanPostProcessor} bean capable of processing Spring's @{@link me.wbean.spring.starter.nsq.core.consumer.NsqListener} annotation
 * also register a default {@link NsqListenerEndpointRegister} hole all Listener Container
 *
 * <p>class get default dcc server address from environment's active profiles </p>
 *
 * @see NsqProducerContainer
 * @see NsqTemplate
 * @see NsqListenerAnnotationBeanPostProcessor
 * @see NsqListenerEndpointRegister
 *
 * Created by wbean on 2017/8/10
 */
@Configuration
@EnableConfigurationProperties(NsqProperties.class)
public class NsqAutoConfiguration {

    private NsqProperties nsqProperties;

    private Environment environment;

    public NsqAutoConfiguration(NsqProperties nsqProperties){
        this.nsqProperties = nsqProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public NsqProducerContainer nsqProducerContainer(){
        return new NsqProducerContainer(nsqProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NsqTemplate nsqTemplate(){
        return new NsqTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public NsqListenerAnnotationBeanPostProcessor nsqListenerAnnotationBeanPostProcessor(){
        return new NsqListenerAnnotationBeanPostProcessor(nsqProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public NsqListenerEndpointRegister nsqListenerEndpointRegister(){
        return new NsqListenerEndpointRegister();
    }
}
