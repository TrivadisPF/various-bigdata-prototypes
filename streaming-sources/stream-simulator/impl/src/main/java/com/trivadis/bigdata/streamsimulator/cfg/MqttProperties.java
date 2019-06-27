package com.trivadis.bigdata.streamsimulator.cfg;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * MQTT specific configuration properties
 * 
 * @author Markus Zehnder
 */
@Component
@ConfigurationProperties(prefix = "simulator.mqtt.producer")
@Getter
@Setter
public class MqttProperties {
    private String[] serverURIs = { "tcp://localhost:1883" };
    private String userName;
    private char[] password;
    private boolean async = true;
    private String defaultTopic;
    private String clientName = "streamsimulator";
    private int maxInflight = MqttConnectOptions.MAX_INFLIGHT_DEFAULT;

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

}
