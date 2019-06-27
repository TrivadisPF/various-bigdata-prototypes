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
-s 		Sink to use, one of MQTT, KAFKA, CONSOLE
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
```

It can either be run through Maven or as a Docker container. 

### Run it as a Java Program

Write a all in on messages to MQTT in CSV format

```
mvn exec:java -Dexec.args="-s MQTT -h mosquitto-1 -p 1883 -f CSV"
```

Write all in one messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h broker-1 -p 9092 -f CSV"
```

Write separate messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h broker-1 -p 9092 -f CSV -m SPLIT"
```

Write separate messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h broker-1 -p 9092 -f CSV -m SPLIT"
```


### Run it as a docker container

Write a all in on messages to MQTT in CSV format

```
docker run --network docker_default trivadis/iot-truck-simulator '-s' 'MQTT' '-h' 'mosquitto-1' '-p' '1883' '-f' 'CSV'
```

Write a all in on messages to KAFKA in CSV format

```
docker run --network docker_default trivadis/iot-truck-simulator '-s' 'KAFKA' '-h' 'broker-1' '-p' '9092' '-f' 'CSV'
```

Write a all in on messages to JMS (ActiveMQ) in CSV format

```
docker run --network docker_default trivadis/iot-truck-simulator '-s' 'JMS' '-h' 'activemq' '-p' '61616' '-f' 'CSV'
```