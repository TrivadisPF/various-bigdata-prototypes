# IoT Truck Simulator

## Building from the source

```
mvn package -Dmaven.test.skip=true
```

To publish it to Docker Hub

```
export PASSWORD=xxxxx
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
```

It can either be run through mavan or as a docker container. 

### Run it as a Java Program

Write a single message to MQTT in CSV format

```
mvn exec:java -Dexec.args="-s MQTT -h mosquitto-1 -p 1883 -f CSV"
```

Write a single message to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h broker-1 -p 9092 -f CSV"
```

Write two messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h broker-1 -p 9092 -f CSV -m SPLIT"
```

Write two messages to KAFKA in CSV format

```
mvn exec:java -Dexec.args="-s KAFKA -h broker-1 -p 9092 -f CSV -m SPLIT"
```


### Run it as a docker container

Write a single message to MQTT in CSV format

```
docker run --network analyticsplatform_default gschmutz/iot-truck-simulator '-s' 'MQTT' '-h' 'mosquitto-1' '-p' '1883' '-f' 'CSV'
```