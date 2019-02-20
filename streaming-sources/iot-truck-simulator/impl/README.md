# IoT Truck Simulator

## Buidling 


```
export PASSWORD=xxxxx
mvn compile package -Dmaven.test.skip=true jib:build -Djib.to.auth.password=$PASSWORD
```




```
mvn exec:java -Dexec.args="-s MQTT -f JSON -m COMBINE -t sec"
```

```

```


## Running the Truck Simulator

```
-s MQTT | KAFKA
-h <host-ip>
-p <port>
-f JSON | CSV | AVRO
-m COMBINE | SPLIT
-t SEC | MS
```

### Run it as a Java Program

```
mvn exec:java -Dexec.args="-s MQTT -f CSV -p 1883"
```

### Run it in a container

To run it as a container instead:

```
docker run --network analyticsplatform_default  gschmutz/iot-truck-simulator '-s' 'MQTT' '-p' '1883' '-h' 'mosquitto-1' '-f' 'CSV'
```