package me.wbean.spring.starter.nsq.core.consumer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Helper bean for registering {@link MethodNsqListenerEndpoint} with
 * a {@link NsqListenerEndpointRegister}.
 *
 *
 * Created by wbean on 2017/8/17
 */
public class NsqListenerEndpointRegistrar implements InitializingBean {
    private NsqListenerEndpointRegister endpointRegister;

    private final List<NsqListenerEndpointDescriptor> endpointDescriptors = new ArrayList<>();

    public NsqListenerEndpointRegister getEndpointRegister() {
        return endpointRegister;
    }

    public void setEndpointRegister(NsqListenerEndpointRegister endpointRegister) {
        this.endpointRegister = endpointRegister;
    }

    @Override
    public void afterPropertiesSet() {
        registerAllEndpoints();
    }

    public void registerAllEndpoints() {
        synchronized (this.endpointDescriptors) {
            for (NsqListenerEndpointDescriptor descriptor : this.endpointDescriptors) {
                this.endpointRegister.registerListenerContainer(
                        descriptor.endpoint);
            }
        }
    }

    /**
     * register a new {@link MethodNsqListenerEndpoint}
     *
     * @param endpoint
     */
    public void registerEndpoint(MethodNsqListenerEndpoint endpoint){
        Assert.notNull(endpoint, "Endpoint must be set");
        NsqListenerEndpointDescriptor descriptor = new NsqListenerEndpointDescriptor(endpoint);
        synchronized (this.endpointDescriptors){
            this.endpointDescriptors.add(descriptor);
        }
    }


    /**
     * data object, hold a {@link MethodNsqListenerEndpoint}
     */
    private static final class NsqListenerEndpointDescriptor{
        private MethodNsqListenerEndpoint endpoint;

        public NsqListenerEndpointDescriptor(MethodNsqListenerEndpoint endpoint) {
            this.endpoint = endpoint;
        }

        public MethodNsqListenerEndpoint getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(MethodNsqListenerEndpoint endpoint) {
            this.endpoint = endpoint;
        }
    }
}
