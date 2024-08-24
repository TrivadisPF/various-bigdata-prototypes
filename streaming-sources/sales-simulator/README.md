# Sales Simulator

This project's highly configurable, Python-based, synthetic data generator (producer.py) streams product listings, sales transactions, and inventory restocking activities to Apache Kafka topics.

It is based on the excellent work of Gary Stafford available here: <https://github.com/garystafford/streaming-sales-generator>.

The following changes have been made to the original implementation:

 - configuration using environment variables
 - docker image mave available `trivadis/sales-simulator:latest`
 - publish Kafka message with key (`product_id`)
 - refactor to use the `confluent-kafka` python library  

```bash
docker run -e KAFKA_BOOTSTRAP_SERVERS=localhost:9092 trivadis/sales-simulator:1.0.0
```

You can use the following environment variables to override default settings

| Varialbe  | Default  | Description  |
|-----------|-----------|-----------|
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Specify the endpoint of the Kafka Bootstrap Broker(s) |
| `KAFKA_SCHEMA_REGISTRY_URL` | `http://localhost:9092` | Specify the endpoint of the Schema Registry |
| `KAFKA_MESSAGE_FORMAT` | `json` | The message format to use when serializing the messages, either `json` or `avro`. |
| `KAFKA_TOPIC_PRODUCTS` | `demo.products` | Name of the Kafka topic holding product listings |
| `KAFKA_TOPIC_PURCHASES` | `demo.purchases` | Name of the Kafka topic holding sales transactions |
| `KAFKA_TOPIC_PURCHASES` | `demo.inventories` | Name of the Kafka topic holding restocking activities |
