package me.wbean.spring.starter.nsq.core.consumer;


import java.lang.reflect.Method;

import me.wbean.spring.starter.nsq.core.NsqProperties;

/**
 * All data about the NsqListener annotated method
 *
 * Created by wbean on 2017/8/16
 */
public class MethodNsqListenerEndpoint {
    NsqProperties nsqProperties;

    private String topic;

    private String channel;

    private boolean autoFinish;

    private int rdy;

    private Object bean;

    private Method method;

    private NsqListenerErrorHandler nsqListenerErrorHandler;

    private NsqMessageDecoder nsqMessageDecoder;

    private NsqRequeuePolicy nsqRequeuePolicy;


    public String getUniqueId(){
        return topic +":"+channel;
    }

    public NsqProperties getNsqProperties() {
        return nsqProperties;
    }

    public void setNsqProperties(NsqProperties nsqProperties) {
        this.nsqProperties = nsqProperties;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public NsqListenerErrorHandler getNsqListenerErrorHandler() {
        return nsqListenerErrorHandler;
    }

    public void setNsqListenerErrorHandler(NsqListenerErrorHandler nsqListenerErrorHandler) {
        this.nsqListenerErrorHandler = nsqListenerErrorHandler;
    }

    public boolean isAutoFinish() {
        return autoFinish;
    }

    public void setAutoFinish(boolean autoFinish) {
        this.autoFinish = autoFinish;
    }

    public void setRdy(int rdy){
        this.rdy = rdy;
    }

    public int getRdy(){
        return this.rdy;
    }

    public NsqMessageDecoder getNsqMessageDecoder() {
        return nsqMessageDecoder;
    }

    public void setNsqMessageDecoder(NsqMessageDecoder nsqMessageDecoder) {
        this.nsqMessageDecoder = nsqMessageDecoder;
    }

    public NsqRequeuePolicy getNsqRequeuePolicy() {
        return nsqRequeuePolicy;
    }

    public void setNsqRequeuePolicy(NsqRequeuePolicy nsqRequeuePolicy) {
        this.nsqRequeuePolicy = nsqRequeuePolicy;
    }
}
