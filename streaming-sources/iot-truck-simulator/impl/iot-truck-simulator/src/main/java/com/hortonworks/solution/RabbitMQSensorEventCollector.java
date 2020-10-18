package com.hortonworks.solution;

import com.hortonworks.simulator.impl.domain.transport.MobileEyeEvent;
import com.rabbitmq.client.Channel;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class RabbitMQSensorEventCollector extends AbstractSensorEventCollector {

	// Adapter default configuration
	private String uri;
	private String exchange;

	private com.rabbitmq.client.ConnectionFactory factory;
	private com.rabbitmq.client.Connection connection;
	Channel channel;

    private static final String DESTINATION_NAME_TRUCK_POSITION = "test.queue.truck_position";
    private static final String DESTINATION_NAME_TRUCK_DRIVING_INFO = "test.queue.truck_driving_info";

	private Logger logger = Logger.getLogger(this.getClass());

	MessageProducer producer = null;
	Session session = null;

	private void connect() throws URISyntaxException {
		factory = new com.rabbitmq.client.ConnectionFactory();
		try {
			factory.setUri(uri);
		} catch (Exception e) {
			System.err.println(e.toString());
			throw new URISyntaxException(uri, e.toString());
		}
    }

	public RabbitMQSensorEventCollector() throws URISyntaxException {

		if (factory == null) {
			connect();
		}
	}

	@Override
	protected String getTopicName(Integer eventKind, MobileEyeEvent originalEvent) {
		String topicName = null;
		if (eventKind.equals(MobileEyeEvent.EVENT_KIND_BEHAVIOUR_AND_POSITION)) {
			topicName = DESTINATION_NAME_TRUCK_POSITION;
		} else if (eventKind.equals(MobileEyeEvent.EVENT_KIND_POSITION)) {
			topicName = DESTINATION_NAME_TRUCK_POSITION;
		} else if (eventKind.equals(MobileEyeEvent.EVENT_KIND_BEHAVIOUR)) {
			topicName = DESTINATION_NAME_TRUCK_DRIVING_INFO;
		}
		return topicName;	
	}

	@Override
	protected void sendMessage(String topicName, MobileEyeEvent originalEvent, Object message) {
		if (Lab.vehicleFilters != null && Lab.vehicleFilters.contains(originalEvent.getTruck().getTruckId())
				|| Lab.vehicleFilters == null) {
			String truckId = String.valueOf(originalEvent.getTruck().getTruckId());

			try {
				logger.info("Connecting to: " + factory.getHost() + ":" + factory.getPort() + factory.getVirtualHost() + "    exchange: " + exchange);
				connection = factory.newConnection();
				logger.info("Connected");
				channel = connection.createChannel();
				channel.exchangeDeclare(exchange, "topic");

				channel.basicPublish(exchange, topicName, null, message.toString().getBytes());

			} catch (Exception e) {
				System.err.println(e.toString());
			} finally {
				try {
					if (channel != null) {
						channel.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (IOException e) {
					//
				} catch (TimeoutException e) {
					//
				}
				logger.info("Disconnected");
			}

		}
	}
}
