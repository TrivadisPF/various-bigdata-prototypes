# Removing old (expired) messages from a stream of data
This project will implement a solution, that assures that only new (by timestamp and key) messages will be passed onwards to the sink topic. 

## Context

Situations we **need** to solve with this project:

* -

Situations we **do not have** to solve with this project:

* -

## High-Level overview

...

## Requirements

1. Messages are serialized avro objects (both key and value) and have to be passed onwards without any changes
2. Solution should be as generic as possible
3. ---
 
## References

1. [EventDeduplicationLambdaIntegrationTest](https://www.javatips.net/api/examples-master/kafka-streams/src/test/java/io/confluent/examples/streams/EventDeduplicationLambdaIntegrationTest.java) by KafkaStreams Examples

## Implementation
See [here](./impl/README) for the implementation.
