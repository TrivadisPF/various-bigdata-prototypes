# Streaming Message Simulator
This project will implement a solution for consistently and repeatedly simulate event messages of various types to various streaming sinks. 

## Context

What we **need** to solve with this project:

* simulator should produce messages based on time

What we **do not have** to solve with this project:

* -

## High-Level overview

...

## Requirements

### Must-Have

1. It should be possible to easily provide a set of messages to reply with the simulator
2. Different message types should be supported in a set of messages
2. Simulator should use a timestamp to derive the time and it should be configurable up to which parts the time (ms, sec, min, hour) should be used and on which base it should be "added"
3. Target for messages should be at least Apache Kafka
4. Various formats should be supported: Avro, JSON, CSV, ...


### Optional
1. Target for simulated messages should be pluggable (not only Kafka, but also MQTT, ActiveMQ, ...)

## References

1. [EventDeduplicationLambdaIntegrationTest](https://www.javatips.net/api/examples-master/kafka-streams/src/test/java/io/confluent/examples/streams/EventDeduplicationLambdaIntegrationTest.java) by KafkaStreams Examples

## Implementation
See [here](./impl/README.md) for the implementation.
