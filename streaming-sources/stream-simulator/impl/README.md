# Streaming Message Simulator

_Work in progress._
Currently in setup and exploration phase.

Tasks and milestones:

- [x] Maven project structure with Spring Boot application shell
- [x] Sending static test data in a simple format (e.g. CSV) to a Kafka topic
- [x] Throttling of message sending (x msg/sec)
- [x] Configurable speedup and slowdown based on timestamp in input data

      (e.g. 1h in input data relates to 1m for sending data)

- [ ] Speedup and slowdown with processing queue to avoid reading all messages into memory
- [ ] Multiple topics
- [ ] Docker image
- [ ] Avro support
- [ ] Various input data formats (CSV, XML, Excel, generic files)
- [ ] ...

Optional features:

- [ ] Web interface
- [ ] Pluggable targets (not only Kafka, but also MQTT, StreamSets, ...)
- [ ] Date value adjustments in input data based on a reference date
- [ ] ...

This project page will be updated with detailed information after the first proof of concepts have been done.

## Technology stack

- Java 8
- [Spring Boot 2.1](https://spring.io/projects/spring-boot)
- [Spring Integeration](https://spring.io/projects/spring-integration)
- [Kafka 2.1](https://kafka.apache.org/)
- [Docker CE](https://www.docker.com/)
- Maven 3.6

### Framework evaluation
The following tools and framworks are still evaluated:

- [Bucket4j - rate-limiting library based on token-bucket algorithm](https://github.com/vladimir-bukhtoyarov/bucket4j)
- Ringbuffer like the [LMAX Disruptor](http://lmax-exchange.github.io/disruptor/)
  [LMAX Architecture article by Martin Fowler](https://martinfowler.com/articles/lmax.html)

## Using Docker environment

TODO

### Spring Boot application

TODO

#### Dockerizing Spring Boot application

TODO

### Building the project

```
mvn package
```
