package me.wbean.spring.starter.nsq.core.consumer;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQMessage;
import com.github.brainlag.nsq.callbacks.NSQMessageCallback;

import me.wbean.spring.starter.nsq.exception.ListenerExecutionBusinessFailedException;
import me.wbean.spring.starter.nsq.exception.ListenerExecutionFailedException;
import me.wbean.spring.starter.nsq.exception.ListenerExecutionInvokeFailedException;

/**
 * Invoke annotated consume method and process error handler.
 *
 * Created by wbean on 2017/8/15
 */
public class DefaultMessageHandler implements NSQMessageCallback {
    private static final Log log = LogFactory.getLog(DefaultMessageHandler.class);

    private Object bean;

    private Method method;

    private NSQConsumer consumer;

    private NsqListenerErrorHandler nsqListenerErrorHandler;

    private NsqMessageDecoder nsqMessageDecoder;

    private NsqRequeuePolicy nsqRequeuePolicy;

    private Boolean autoFinish;

    public void setConsumer(NSQConsumer consumer) {
        this.consumer = consumer;
    }

    public void setNsqListenerErrorHandler(NsqListenerErrorHandler nsqListenerErrorHandler) {
        this.nsqListenerErrorHandler = nsqListenerErrorHandler;
    }

    public void setNsqMessageDecoder(NsqMessageDecoder nsqMessageDecoder) {
        this.nsqMessageDecoder = nsqMessageDecoder;
    }

    public void setNsqRequeuePolicy(NsqRequeuePolicy nsqRequeuePolicy) {
        this.nsqRequeuePolicy = nsqRequeuePolicy;
    }

    public void setAutoFinish(Boolean autoFinish) {
        this.autoFinish = autoFinish;
    }

    public DefaultMessageHandler(Object bean, Method method){
        this.bean = bean;
        this.method = method;
    }

    public void process(NSQMessage message) {

        try {
            this.doInvoke(message);
            if(this.autoFinish){
                message.finished();
            }
        } catch (ListenerExecutionFailedException e) {
            if (nsqListenerErrorHandler != null) {
                nsqListenerErrorHandler.handleError(message, e, consumer);
            }

            if (nsqRequeuePolicy != null){
                nsqRequeuePolicy.requeue(consumer, message);
            }

            if (e.getCause() instanceof RuntimeException){
                throw (RuntimeException) e.getCause();
            }else{
                throw e;
            }
        }
    }


    private void doInvoke(NSQMessage message) throws ListenerExecutionFailedException {
        try {
            if(method.getParameterCount() < 1 || method.getParameterCount() > 3){
                throw new IllegalArgumentException("illegal argument count");
            }

            Object messageObj = parseMessageObj(message);

            switch (method.getParameterCount()){
                case 1:
                    method.invoke(bean, messageObj);
                    break;
                case 2:
                    method.invoke(bean, messageObj, message);
                    break;
                case 3:
                    method.invoke(bean, messageObj, message, consumer);
                    break;
            }

        } catch (IllegalAccessException | IllegalArgumentException e) {
            log.error("Failed to invoke handler method", e);
            throw new ListenerExecutionInvokeFailedException("Failed to invoke handler method", e);
        } catch (InvocationTargetException e1) {
            log.error("Business Exception when invoke handler method", e1);
            throw new ListenerExecutionBusinessFailedException("Business Exception when invoke handler method", e1.getTargetException());
        } catch (Throwable t1){
            log.error("Other Exception when invoke handler method", t1);
        }
    }

    /**
     * try decode message depends on message class type
     *
     * default use
     * @see com.alibaba.fastjson.JSON
     *
     * @param message
     * @return
     */
    private Object parseMessageObj(NSQMessage message) {
        Object ret = message;

        Class [] parameterClass = method.getParameterTypes();

        Class messageClass = parameterClass[0];

        if(nsqMessageDecoder != null){
            return nsqMessageDecoder.decode(message);
        }

        if(! NSQMessage.class.equals(messageClass)){
            String messageBody = new String(message.getMessage(), Charset.forName("utf-8"));

            if(String.class.equals(messageClass)){
                return messageBody;
            }else{
                try {
                    ret = JSON.parseObject(messageBody, messageClass);
                }catch (Exception e){
                    log.error(String.format("NSQMessage can't parse to JSONObject, NSQMessage=%s, ClassType=%s", messageBody, messageClass.getName()));
                    return null;
                }
            }

        }
        return ret;
    }

    @Override
    public void message(NSQMessage nsqMessage) {
        process(nsqMessage);
    }
}
