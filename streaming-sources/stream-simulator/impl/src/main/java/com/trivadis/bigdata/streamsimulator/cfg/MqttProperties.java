package com.trivadis.bigdata.streamsimulator.cfg;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MQTT specific configuration properties
 * 
 * @author mzehnder
 */
@Component
@ConfigurationProperties(prefix = "simulator.mqtt.producer")
public class MqttProperties {
    private String[] serverURIs = { "tcp://localhost:1883" };
    private String userName;
    private char[] password;
    private boolean async = true;
    private String defaultTopic;
    private String clientName = "streamsimulator";
    private int maxInflight = MqttConnectOptions.MAX_INFLIGHT_DEFAULT;

    public String[] getServerURIs() {
        return serverURIs;
    }

    public void setServerURIs(String[] serverURIs) {
        this.serverURIs = serverURIs;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(serverURIs);
        if (!StringUtils.isBlank(userName)) {
            options.setUserName(userName);
        }
        if (!(password == null || password.length == 0)) {
            options.setPassword("password".toCharArray());
        }
        options.setMaxInflight(maxInflight);
        return options;
    }

    public int getMaxInflight() {
        return maxInflight;
    }

    public void setMaxInflight(int maxInflight) {
        this.maxInflight = maxInflight;
    }

}
