# Consuming Kafka Avro Messages from Spark

This project demonstrates how to read Avro-formatted messages from a Kafka topic using Spark 2.3.x.

It uses the following UDF to get the Avro schema from the Confluent schema registry and deserializes the Avro using the Confluent Avro deserializer:

<https://github.com/tubular/confluent-spark-avro>

This 

## Avro Message Generator


```
mvn exec:java
```

```
docker exec -ti schema-registry kafka-avro-console-consumer --bootstrap-server broker-1:9092 --topic sensor-group-1-v1
```

## Spark Implementation

Add the spark-sql-kafka library to the `--packages`

```
org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.2
```

There is a problem with a conflict of the lz4 library, therefore it has to be excluded using the `--exclude-packages`

```
net.jpountz.lz4:lz4
```



```
org.apache.spark:confluent-spark-avro-assembly:1.2
```

```
%spark.dep
z.load("/tmp/confluent-spark-avro-assembly-1.2.jar")
```


```
val df = spark.read.
	format("kafka").
	option("kafka.bootstrap.servers", "broker-1:9092,broker-2:9093").
	option("subscribe", "sensor-group-1-v1").
	load()
```

```
val utils = new com.databricks.spark.avro.ConfluentSparkAvroUtils("http://schema-registry:8081")
```

```
//val keyDeserializer = utils.deserializerForSubject("sensor-group-1-v1-key")
val valueDeserializer = utils.deserializerForSubject("sensor-group-1-v1-value")
```

```
df.select(
    (col("key").alias("key")),
    valueDeserializer(col("value").alias("value"))
).show(10)
```