package com.hortonworks.solution;

import com.hortonworks.simulator.impl.domain.transport.MobileEyeEvent;
import com.microsoft.azure.sdk.iot.device.*;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

public class AzureIoTHubSensorEventCollector extends AbstractSensorEventCollector {

	// Using the MQTT protocol to connect to IoT Hub
	private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
	private static DeviceClient client;

	//private MqttClient sampleClient = null;
	private static final String TOPIC = "truck";
	//private int qos = 2;
	//private String broker = "tcp://" + Lab.host + ":" + ((Lab.port == null) ? "1883" : Lab.port);
	//private String clientId = "TrucksProducer";

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	protected String getTopicName(Integer eventKind, MobileEyeEvent originalEvent) {
		String topicName = null;
		return topicName;
	}

	public AzureIoTHubSensorEventCollector() throws IOException, URISyntaxException {
		String connString = "HostName=" + Lab.host + ";DeviceId=" + Lab.deviceId +  ";SharedAccessKey=" + Lab.accessKey;

		System.out.println(connString);

		// Connect to the IoT hub.
		client = new DeviceClient(connString, protocol);
		client.open();
	}

	// Print the acknowledgement received from IoT Hub for the telemetry message sent.
	private static class EventCallback implements IotHubEventCallback {
		public void execute(IotHubStatusCode status, Object context) {
			System.out.println("IoT Hub responded to message with status: " + status.name());

			if (context != null) {
				synchronized (context) {
					context.notify();
				}
			}
		}
	}
	
	@Override
	protected void sendMessage(String topicName, MobileEyeEvent originalEvent, Object message) {
		if (Lab.vehicleFilters != null && Lab.vehicleFilters.contains(originalEvent.getTruck().getTruckId())
				|| Lab.vehicleFilters == null) {
			String eventToPass = (String) message;
			if (client != null) {
				Message msg = new Message(eventToPass);

				// Add a custom application property to the message.
				// An IoT hub can filter on these properties without access to the message body.
				//msg.setProperty("truckId", (currentTemperature > 30) ? "true" : "false");

				Object lockobj = new Object();
				msg.setMessageId(UUID.randomUUID().toString());
				msg.setProperty("vehicleId", String.valueOf(originalEvent.getTruck().getTruckId()));

				// Send the message.
				EventCallback callback = new EventCallback();
				client.sendEventAsync(msg, callback, lockobj);
				synchronized (lockobj) {
					try {
						lockobj.wait();
					} catch (InterruptedException e) {
						logger.error("Error sending event[" + eventToPass + "] to Azure IoT Hub", e);
					}
				}
			} else {
				logger.error("Error client is null");
			}
		}
	}
	

}
