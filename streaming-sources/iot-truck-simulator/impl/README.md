# IoT Truck Simulator

## Building from source

```
mvn package -Dmaven.test.skip=true
```

To publish it to Docker Hub (using Maven support by [Jib](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#quickstart))

```
export PASSWORD="xxxxx"
mvn compile package -Dmaven.test.skip=true jib:build -Djib.to.auth.password=$PASSWORD
```

## Running the Truck Simulator

The simulator accepts the following arguments:

```
-s 		Sink to use, one of FILE, MQTT, KAFKA, JMS, AZURE_IOTHUB, CONSOLE
-h 		Host address (of the broker in case of MQTT and KAFKA)
-p 		Port (of the broker in case of MQTT and KAFKA)
-f 		Message format to use, one of JSON, CSV
-m 		One or more messages 
		- COMBINE = produce a single message with both position and eventType (default)
		- SPLIT = produce one message for positions and one for eventType
-t 		Precision of timestamp
		- SEC = seconds (default)
		- MS = milliseconds
-mt 	JMS Message Type (only if JMS is used) 
		- TEXT = produce a JMS TextMessage		- MAP = produce a JMS MapMessage
		- MAP = produce a JMS BytesMessage
-d 		Delay in milliseconds between sending a message for each truck in the fleet
-fs 	Fleet size
-fpv	File per Vehicle (only when Sink is FILE)
-vf		Vehicle Filter
-did   DeviceId for Azure IoT Hub and AWS IoT Core
-ak    access key for Azure IoT Hub
-cf    Certificate File
-pkf   Private Key File
-es    Event Schema to be used, either
       - 1 = produce with Schema 1 (latitude and longitude as separate fields)
       - 2 = produce with Schema 2 (additional system field and latLong together as one string)
```

It can either be run through Maven or as a Docker container. 

### Run it from Maven

Write a all in on messages to MQTT in CSV format

```
mvn exec:java -Dexec.args="-s MQTT -h mosquitto-1 -p 1883 -f CSV"
```

Write all in one messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h kafka-1 -p 19092 -f CSV"
```

Write separate messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h kafka-1 -p 19092 -f CSV -m SPLIT"
```

Write separate messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h broker-1 -p 9092 -f CSV -m SPLIT"
```


### Run it as a docker container

Write a all in on messages to MQTT in CSV format

```
docker run --network docker_default --rm trivadis/iot-truck-simulator '-s' 'MQTT' '-h' 'mosquitto-1' '-p' '1883' '-f' 'CSV'
```

Write a all in on messages to KAFKA in CSV format

```
docker run --network docker_default --rm trivadis/iot-truck-simulator '-s' 'KAFKA' '-h' 'kafka-1' '-p' '19092' '-f' 'CSV'
```

Write a all in on messages to JMS (ActiveMQ) in CSV format

```
docker run --network docker_default --rm trivadis/iot-truck-simulator '-s' 'JMS' '-h' 'activemq' '-p' '61616' '-f' 'CSV'
```

Write a all in on messages to Azure IoT Hub in JSON format

```
docker run --rm trivadis/iot-truck-simulator '-s' 'AZURE_IOTHUB' '-h' 'iothubgus.azure-devices.net' '-did' 'truck-simulator-1' '-ak' 'gbXEnEKF/GfFhzttAfVHLXTTDra1cP9seDQy9XJY534=' '-f' 'JSON' '-vf' '10-12' 
```

Write a all in on messages to AWS IoT Core in JSON format


```
docker run --rm -v ${PWD}:/data-transfer/  trivadis/iot-truck-simulator '-s' 'AWS_IOTCORE' '-h' 'a202kffyw14c3u-ats.iot.eu-central-1.amazonaws.com' '-did' 'sdk-java' '-cf' '/data-transfer/Truck10.cert.pem' '-pkf' '/data-transfer/Truck10.private.key' '-f' 'JSON'
```

HostName=iothubgus.azure-devices.net;DeviceId=iotdevice-2;SharedAccessKey=mixJQkAsl5qyXuLgDjDBS4Dh7jzJD8kGhpr4NP+2/Uo=
