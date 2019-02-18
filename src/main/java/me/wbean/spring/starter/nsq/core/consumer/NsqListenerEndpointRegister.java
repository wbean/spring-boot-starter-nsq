package me.wbean.spring.starter.nsq.core.consumer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

/**
 * Creates the necessary {@link NsqMessageListenerContainer} instances for the
 * registered {@linkplain MethodNsqListenerEndpoint endpoints}. Also manages the
 * lifecycle of the listener containers, in particular within the lifecycle
 * of the application context.
 *
 * Created by wbean on 2017/8/16
 */
public class NsqListenerEndpointRegister implements DisposableBean, SmartLifecycle, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {
    private static final Log log = LogFactory.getLog(NsqListenerEndpointRegister.class);

    private final Map<String, NsqMessageListenerContainer> listenerContainers = new ConcurrentHashMap<>();

    private int phase = Integer.MAX_VALUE;

    private ConfigurableApplicationContext applicationContext;

    private boolean contextRefreshed;


    /**
     * Return the managed {@link NsqMessageListenerContainer} instance(s).
     * @return the managed {@link NsqMessageListenerContainer} instance(s).
     */
    public Collection<NsqMessageListenerContainer> getListenerContainers() {
        return Collections.unmodifiableCollection(this.listenerContainers.values());
    }


    /**
     * Create a message listener container for the given {@link MethodNsqListenerEndpoint}.
     * <p>This create the necessary infrastructure to honor that endpoint
     * with regards to its configuration.
     * @param endpoint the endpoint to add
     */
    public void registerListenerContainer(MethodNsqListenerEndpoint endpoint) {
        Assert.notNull(endpoint,"can't register listener with empty MethodNsqListenerEndpoint");
        String id = endpoint.getUniqueId();
        synchronized (this.listenerContainers) {
            Assert.state(!this.listenerContainers.containsKey(id),"Another endpoint is already registered with unique id: " + id);

            NsqMessageListenerContainer container = createListenerContainer(endpoint);

            this.listenerContainers.put(id, container);
        }
    }

    private NsqMessageListenerContainer createListenerContainer(MethodNsqListenerEndpoint endpoint){
        NsqMessageListenerContainer container = new NsqMessageListenerContainer(endpoint);

        return container;
    }


    @Override
    public void destroy() throws Exception {
        this.getListenerContainers().stream().forEach(
                (container)->{
                    if(container instanceof DisposableBean){
                        try{
                            ((DisposableBean) container).destroy();
                        }catch (Exception e){
                            log.warn("Failed to destroy message listener container", e);
                        }
                    }
                }
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().equals(this.applicationContext)) {
            this.contextRefreshed = true;
        }
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        this.getListenerContainers().forEach(
                (container)->{
                    container.stop(runnable);
                }
        );
        runnable.run();
    }

    @Override
    public void start() {
        this.getListenerContainers().forEach(
                this::startIfNecessary
        );
    }

    /**
     * Start the specified {@link NsqMessageListenerContainer} if it should be started
     * on startup.
     * @param listenerContainer the listener container to start.
     * @see NsqMessageListenerContainer#isAutoStartup()
     */
    private void startIfNecessary(NsqMessageListenerContainer listenerContainer) {
        if (this.contextRefreshed || listenerContainer.isAutoStartup()) {
            listenerContainer.start();
        }
    }

    @Override
    public void stop() {
        this.getListenerContainers().forEach(
                NsqMessageListenerContainer::stop
        );
    }

    @Override
    public boolean isRunning() {
        for (NsqMessageListenerContainer container : this.getListenerContainers()) {
            if (container.isRunning()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getPhase() {
        return this.phase;
    }
}
