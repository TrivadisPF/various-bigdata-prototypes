package com.hortonworks.solution;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.hortonworks.simulator.impl.domain.transport.MobileEyeEvent;

public class ActiveMQSensorEventCollector extends AbstractSensorEventCollector {

    private static final Boolean NON_TRANSACTED = false;
    private static final long MESSAGE_TIME_TO_LIVE_MILLISECONDS = 0;
    private static final String DESTINATION_NAME_TRUCK_POSITION = "test.queue.truck_position";
    private static final String DESTINATION_NAME_TRUCK_DRIVING_INFO = "test.queue.truck_driving_info";
	
	private Logger logger = Logger.getLogger(this.getClass());

	MessageProducer producer = null;
	Session session = null;

	private void connect() {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + Lab.host + ":" + Lab.port);

            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
        } catch (Throwable t) {
        	System.err.println(t.getMessage());
			t.printStackTrace();
        }
    }

	private void createProducer(Integer eventKind) {
        Connection connection = null;

        try {
            Destination destination = session.createQueue(getTopicName(eventKind, null));

            producer = session.createProducer(destination);
            producer.setTimeToLive(MESSAGE_TIME_TO_LIVE_MILLISECONDS);

        } catch (Throwable t) {
        	System.err.println(t.getMessage());
			t.printStackTrace();
        }
    }

	public ActiveMQSensorEventCollector() {

		if (session == null) {
			connect();
		}
		if (producer == null) {
			createProducer(MobileEyeEvent.EVENT_KIND_BEHAVIOUR_AND_POSITION);
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

			if (Lab.messageType.equals(Lab.TEXT)) {
				TextMessage textMessage;
				try {
					textMessage = session.createTextMessage((String) message);
					textMessage.setStringProperty("truckId", truckId);

					if (producer != null) {
						producer.send(textMessage);
					}

				} catch (JMSException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}

			if (Lab.messageType.equals(Lab.MAP)) {
				MapMessage mapMessage;
				try {
					mapMessage = session.createMapMessage();
					mapMessage.setString("message", (String) message);

					if (producer != null) {
						producer.send(mapMessage);
					}
				} catch (JMSException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}

			if (Lab.messageType.equals(Lab.BYTES)) {
				BytesMessage bytesMessage;
				try {
					bytesMessage = session.createBytesMessage();
					bytesMessage.writeBytes(((String) message).getBytes());

					if (producer != null) {
						producer.send(bytesMessage);
					}
				} catch (JMSException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
	}
}
