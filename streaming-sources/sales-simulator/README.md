# Sales Simulator

This project's highly configurable, Python-based, synthetic data generator (producer.py) streams product listings, sales transactions, and inventory restocking activities to Apache Kafka topics.

It is based on the excellent work of Gary Stafford available here: <https://github.com/garystafford/streaming-sales-generator>. 

I have basically wrapped it as a Docker container so that it can be easily started or added to an existing docker-based environment. 

```bash
docker run -e KAFKA_BOOTSTRAP_SERVERS=localhost:9092 trivadis/sales-simulator:1.0.0
```

You can use the following environment variables to override default settings

| Varialbe  | Default  | Description  |
|-----------|-----------|-----------|
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Specify the endpoint of the Kafka Bootstrap Broker(s) |
| `KAFKA_TOPIC_PRODUCTS` | `demo.products` | Name of the Kafka topic holding product listings |
| `KAFKA_TOPIC_PURCHASES` | `demo.purchases` | Name of the Kafka topic holding sales transactions |
| `KAFKA_TOPIC_PURCHASES` | `demo.inventories` | Name of the Kafka topic holding restocking activities |
