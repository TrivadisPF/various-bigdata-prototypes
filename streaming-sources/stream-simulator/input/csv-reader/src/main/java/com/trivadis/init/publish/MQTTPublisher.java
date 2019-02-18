package com.trivadis.init.publish;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.trivadis.init.csv.event.AbstractEvent;

public class MQTTPublisher implements EventPublisher {

	private MqttClient sampleClient = null;
	private static final String TOPIC = "/transportation";
	private int qos = 2;
	private String broker = "tcp://192.168.69.137:1883";
	private String clientId = "TransportationProducer";

	private Logger logger = Logger.getLogger(this.getClass());

	public MQTTPublisher() {
		try {
			sampleClient = new MqttClient(broker, clientId);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			System.out.println("Connecting to MQTT broker: " + broker);
			sampleClient.connect(connOpts);
		} catch (MqttException ex) {
			throw new RuntimeException(ex.getMessage());
		}

	}

	public String topicName(String channelName, AbstractEvent message) {
		return TOPIC + "/" + channelName;
	}

	public void send(String channelName, String message) {
		try {
			System.out.println("Publishing message to MQTT: " + message);
			MqttMessage mqttMessage = new MqttMessage(message.getBytes());
			mqttMessage.setQos(qos);
			sampleClient.publish(topicName(channelName, null), mqttMessage);
			// sampleClient.disconnect();

		} catch (MqttException e) {
			logger.error("Error sending event[" + message + "] to MQTT topic", e);
		}

	}

}
