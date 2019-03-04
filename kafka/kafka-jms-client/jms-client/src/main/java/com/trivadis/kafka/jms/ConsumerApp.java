package com.trivadis.kafka.jms;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import io.confluent.kafka.jms.JMSClientConfig;
import io.confluent.kafka.jms.KafkaConnectionFactory;

public class ConsumerApp {
    public static void main(String[] args) throws JMSException {
        Properties settings = new Properties();
        settings.put(JMSClientConfig.CLIENT_ID_CONFIG, "test-client-2");
        settings.put(JMSClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.73.230:9092");
        settings.put(JMSClientConfig.ZOOKEEPER_CONNECT_CONF, "192.168.73.230:2181");

        ConnectionFactory connectionFactory = new KafkaConnectionFactory(settings);
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination testQueue = session.createQueue("test-jms-queue");

        MessageConsumer consumer = session.createConsumer(testQueue);
        while (true) {
            TextMessage message = (TextMessage)consumer.receive();
            System.out.println(message.getText());
        }
    }
}
