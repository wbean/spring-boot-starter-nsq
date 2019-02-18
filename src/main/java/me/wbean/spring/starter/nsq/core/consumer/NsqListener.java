package me.wbean.spring.starter.nsq.core.consumer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method to be the target of a nsq message listener on the
 * specified topics.
 *
 * Created by wbean on 2017/8/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NsqListener {
    /*
      Topic 名
     */
    String topic();

    /*
      channel 名
     */
    String channel() default "default";


    /*
      是否自动发送ack消息，如果是false，消费消息的方法，需要自己手动调用 finish 方法
     */
    boolean autoFinish() default true;

    /*
      消息处理失败的error handler
     */
    String errorHandler() default "";


    /*
      消息解析器
     */
    String messageDecoder() default "";

    /*
      重试策略
     */
    String requeuePolicy() default "";

    /**
     * rdy
     */
    int rdy() default 0;

}
