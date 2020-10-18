package com.hortonworks.solution;

import com.hortonworks.simulator.impl.domain.transport.MobileEyeEvent;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileSensorEventCollector extends AbstractSensorEventCollector {

	private Logger logger = Logger.getLogger(this.getClass());
    private File file;
    private FileWriter fr;
    private Map<String, BufferedWriter> bufferedWriters = new HashMap<String, BufferedWriter>();

    private BufferedWriter getBufferedWriter(String fileName, String pathname) {
    	String key = pathname + "::" + fileName;

		if (!bufferedWriters.containsKey(key)) {
			file = new File(pathname + fileName);
			try {
				fr = new FileWriter(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			bufferedWriters.put(key, new BufferedWriter(fr));
		}
		return bufferedWriters.get(key);
	}

	private String getFileName(boolean filePerTruck, int truckId) {
    	if (filePerTruck) {
    		return "TruckData-" + truckId + ".dat";
		} else {
			return "TruckData.dat";
		}
	}

	public FileSensorEventCollector() throws MqttException {

	}

	@Override
	protected void sendMessage(String topicName, MobileEyeEvent originalEvent, Object message) {
		if (Lab.vehicleFilters != null && Lab.vehicleFilters.contains(originalEvent.getTruck().getTruckId())
				|| Lab.vehicleFilters == null)  {
			BufferedWriter br = getBufferedWriter(getFileName(Lab.filePerTruck, originalEvent.getTruck().getTruckId()), "/out/");
			try {
				br.write(message + System.getProperty("line.separator"));
				br.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected String getTopicName(Integer eventKind, MobileEyeEvent originalEvent) {
		return null;
	}

}
