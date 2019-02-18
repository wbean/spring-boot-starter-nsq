package me.wbean.spring.starter.nsq.core.consumer;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import me.wbean.spring.starter.nsq.core.NsqProperties;

/**
 * Bean post-processor that registers methods annotated with {@link NsqListener}
 * to be invoked by a nsq message listener container created under the covers
 * according to the parameters of the annotation.
 *
 * Created by wbean on 2017/8/15
 */
public class NsqListenerAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, DisposableBean, SmartInitializingSingleton, EnvironmentAware {
    private static final Log log = LogFactory.getLog(NsqListenerAnnotationBeanPostProcessor.class);

    private BeanFactory beanFactory;

    private NsqProperties nsqProperties;

    private List<NsqMessageListenerContainer> containerList = new ArrayList<NsqMessageListenerContainer>();

    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));

    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();

    private BeanExpressionContext expressionContext;

    private NsqListenerEndpointRegister endpointRegister;

    private final NsqListenerEndpointRegistrar registrar = new NsqListenerEndpointRegistrar();

    private Environment environment;

    public NsqListenerAnnotationBeanPostProcessor(NsqProperties nsqProperties) {
        this.nsqProperties = nsqProperties;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Map<Method, NsqListener> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<NsqListener>) method -> {
                        NsqListener listenerMethods = findListenerAnnotations(method);
                        return listenerMethods;
                    });

            if (annotatedMethods.isEmpty()) {
                nonAnnotatedClasses.add(bean.getClass());
            } else {
                annotatedMethods.entrySet().forEach(entry -> {
                    processNsqListener(entry.getValue(), entry.getKey(), bean, beanName);
                });
            }
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.resolver = ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
            this.expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory, null);
        }
    }

    /**
     * find NsqListener annotation from method
     * <p>
     * a method only support one NsqListener annotation
     *
     * @param method
     * @return
     */
    private NsqListener findListenerAnnotations(Method method) {
        NsqListener ann = AnnotationUtils.findAnnotation(method, NsqListener.class);
        return ann;
    }

    private void processNsqListener(NsqListener nsqListener, Method method, Object bean, String beanName) {
        log.info(String.format("NsqListener load from annotation, class=%s, method=%s", bean.getClass().getName(), method.getName()));
        Method methodToUse = checkProxy(method, bean);

        MethodNsqListenerEndpoint endpoint = new MethodNsqListenerEndpoint();

        try {
            endpoint.setNsqProperties(nsqProperties.clone());
        } catch (CloneNotSupportedException e) {
        }
        endpoint.setBean(bean);
        endpoint.setMethod(methodToUse);
        endpoint.setTopic(this.getValueFromEnvironment(nsqListener.topic()));
        endpoint.setChannel(this.getValueFromEnvironment(nsqListener.channel()));
        endpoint.setAutoFinish(nsqListener.autoFinish());
        endpoint.setRdy(nsqListener.rdy());

        if (StringUtils.hasText(nsqListener.errorHandler())) {
            String errorHandlerBeanName = resolveExpressionAsString(nsqListener.errorHandler(), "errorHandler");
            endpoint.setNsqListenerErrorHandler(this.beanFactory.getBean(errorHandlerBeanName, NsqListenerErrorHandler.class));
        }

        if (StringUtils.hasText(nsqListener.messageDecoder())) {
            String messageDecoderBeanName = resolveExpressionAsString(nsqListener.messageDecoder(), "messageDecoder");
            endpoint.setNsqMessageDecoder(this.beanFactory.getBean(messageDecoderBeanName, NsqMessageDecoder.class));
        }

        if (StringUtils.hasText(nsqListener.requeuePolicy())) {
            String requeuePolicyBeanName = resolveExpressionAsString(nsqListener.requeuePolicy(), "requeuePolicy");
            endpoint.setNsqRequeuePolicy(this.beanFactory.getBean(requeuePolicyBeanName, NsqRequeuePolicy.class));
        }

        this.registrar.registerEndpoint(endpoint);
    }

    private Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                // Found a @NsqListener method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (NoSuchMethodException noMethod) {
                    }
                }
            } catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "@NsqListener method '%s' found on bean target class '%s', " +
                                "but not found in any interface(s) for bean JDK proxy. Either " +
                                "pull the method up to an interface or switch to subclass (CGLIB) " +
                                "proxies by setting proxy-target-class/proxyTargetClass " +
                                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
            }
        }
        return method;
    }

    private String resolveExpressionAsString(String value, String attribute) {
        Object resolved = resolveExpression(value);
        if (resolved instanceof String) {
            return (String) resolved;
        } else {
            throw new IllegalStateException("The [" + attribute + "] must resolve to a String. "
                    + "Resolved to [" + resolved.getClass() + "] for [" + value + "]");
        }
    }

    private Object resolveExpression(String value) {
        String resolvedValue = resolve(value);

        if (!(resolvedValue.startsWith("#{") && value.endsWith("}"))) {
            return resolvedValue;
        }

        return this.resolver.evaluate(resolvedValue, this.expressionContext);
    }

    /**
     * Resolve the specified value if possible.
     *
     * @param value the value to resolve
     * @return the resolved value
     * @see ConfigurableBeanFactory#resolveEmbeddedValue
     */
    private String resolve(String value) {
        if (this.beanFactory != null && this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).resolveEmbeddedValue(value);
        }
        return value;
    }

    @Override
    public void destroy() throws Exception {
        containerList.forEach(container -> container.stop());
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (this.registrar.getEndpointRegister() == null) {
            if (this.endpointRegister == null) {
                Assert.state(this.beanFactory != null,
                        "BeanFactory must be set to find endpoint registry by bean name");
                this.endpointRegister = this.beanFactory.getBean("nsqListenerEndpointRegister", NsqListenerEndpointRegister.class);
            }

            this.registrar.setEndpointRegister(this.endpointRegister);
        }

        this.registrar.afterPropertiesSet();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private String getValueFromEnvironment(String key) {
        if (!(key.startsWith("${") && key.endsWith("}"))) {
            return key;
        }

        String realKey = key.substring(2, key.length() - 1);
        return this.environment.getProperty(realKey);
    }
}
