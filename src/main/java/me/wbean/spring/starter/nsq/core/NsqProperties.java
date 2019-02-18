package me.wbean.spring.starter.nsq.core;


import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.brainlag.nsq.NSQConfig;
import com.github.brainlag.nsq.ServerAddress;

/**
 * Contains runtime properties for nsq config.
 * {@link com.github.brainlag.nsq.NSQConfig}
 * Auto Configure from nsq.*
 *
 * Created by wbean on 2017/8/10
 */
@ConfigurationProperties(prefix = "nsq")
public class NsqProperties implements Cloneable{

    /**
     * 服务lookup地址
     */
    private String host;

    /**
     * 服务lookup端口
     */
    private int port;

    /**
     * 自动ACK消息
     */
    private Boolean autoFinish;

    /**
     * 心跳的毫秒数.
     * 有效范围: 1000 <= heartbeat_interval <= configured_max (-1 禁用心跳)
     * --max-heartbeat-interval (nsqd 标志位) 控制最大值
     * 默认值 --client-timeout / 2
     */
    private Integer heartbeatInterval = null;

    /**
     * 当 nsqd 写到这个客户端时将会用到的缓存的大小（字节数）。
     * 有效范围: 64 <= output_buffer_size <= configured_max (-1 禁用输出缓存)
     * --max-output-buffer-size (nsqd 标志位) 控制最大值
     * 默认值 16kb
     */
    private Integer outputBufferSize = null;

    /**
     * 超时后，nsqd 缓冲的数据都会刷新到此客户端。
     * 有效范围: 1ms <= output_buffer_timeout <= configured_max (-1 禁用 timeouts)
     * --max-output-buffer-timeout (nsqd 标志位) 控制最大值
     * 默认值 250ms
     */
    private Integer outputBufferTimeout = null;

    /**
     * 压缩
     * @see NSQConfig.Compression
     */
    private String compression = null;

    /**
     * 配置 deflate 压缩这次连接的级别
     * --max-deflate-level (nsqd 标志位) 配置允许的最大值
     * 有效范围: 1 <= deflate_level <= configured_max
     * 值越高压缩率越好，但是 CPU 负载也高。
     */
    private Integer deflateLevel = null;

    /**
     * sample_rate (nsqd v0.2.25+) 投递此次连接的消息接收率。
     * 有效范围: 0 <= sample_rate <= 99 (0 禁用)
     * 默认值 0
     */
    private Integer sampleRate = null;

    /**
     * 同时处理消息数
     */
    private Integer maxInFlight = null;

    /**
     * 这个客户端的代理字符串
     */
    private String userAgent = null;

    /**
     * 配置服务端发送消息给客户端的超时时间
     */
    private Integer msgTimeout = null;

    /**
     * 根据配置，生成NSQConfig对象
     *
     * @return
     */
    public NSQConfig getNsqConfig(){
        NSQConfig config = new NSQConfig();
        if(heartbeatInterval != null){
            config.setHeartbeatInterval(heartbeatInterval);
        }

        if(outputBufferSize != null){
            config.setOutputBufferSize(outputBufferSize);
        }

        if(outputBufferTimeout != null){
            config.setOutputBufferTimeout(outputBufferTimeout);
        }

        if(deflateLevel != null){
            config.setDeflateLevel(deflateLevel);
        }

        if(sampleRate != null){
            config.setSampleRate(sampleRate);
        }

        if(maxInFlight != null){
            config.setMaxInFlight(maxInFlight);
        }

        if(userAgent != null){
            config.setUserAgent(userAgent);
        }

        if(msgTimeout != null){
            config.setMsgTimeout(msgTimeout);
        }

        if(compression != null){
            config.setCompression(NSQConfig.Compression.valueOf(compression));
        }

        return config;
    }

    public ServerAddress getServerAddress(){
        return new ServerAddress(host, port);
    }

    @Override
    public NsqProperties clone() throws CloneNotSupportedException {
        return (NsqProperties)super.clone();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Boolean getAutoFinish() {
        return autoFinish;
    }

    public void setAutoFinish(Boolean autoFinish) {
        this.autoFinish = autoFinish;
    }

    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public Integer getOutputBufferSize() {
        return outputBufferSize;
    }

    public void setOutputBufferSize(Integer outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    public Integer getOutputBufferTimeout() {
        return outputBufferTimeout;
    }

    public void setOutputBufferTimeout(Integer outputBufferTimeout) {
        this.outputBufferTimeout = outputBufferTimeout;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public Integer getDeflateLevel() {
        return deflateLevel;
    }

    public void setDeflateLevel(Integer deflateLevel) {
        this.deflateLevel = deflateLevel;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public Integer getMaxInFlight() {
        return maxInFlight;
    }

    public void setMaxInFlight(Integer maxInFlight) {
        this.maxInFlight = maxInFlight;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getMsgTimeout() {
        return msgTimeout;
    }

    public void setMsgTimeout(Integer msgTimeout) {
        this.msgTimeout = msgTimeout;
    }

    @Override
    public String toString() {
        return "NsqProperties{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", autoFinish=" + autoFinish +
                ", heartbeatInterval=" + heartbeatInterval +
                ", outputBufferSize=" + outputBufferSize +
                ", outputBufferTimeout=" + outputBufferTimeout +
                ", compression='" + compression + '\'' +
                ", deflateLevel=" + deflateLevel +
                ", sampleRate=" + sampleRate +
                ", maxInFlight=" + maxInFlight +
                ", userAgent='" + userAgent + '\'' +
                ", msgTimeout=" + msgTimeout +
                '}';
    }
}
