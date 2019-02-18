# Kafka Connect Sink Error Handling
This project will implement a demo for tackling the various errors which can occur in a Kafka Connect Sink. 

## Context

Situations we **need** to solve with this project:

* a message, which is valid should pass
* a message, which is wrongly formatted should be handled
* a message, which is causing an error on the sink resource should be handled

Situations we **do not have** to solve with this project:

* n.a.

## High-Level overview

Kafka Connect is a framework for integrating external systems with Kafka. A Kafka Connect connector can either act as a source (where data is gotten from external and produced into Kafka) or as a sink (where data from Kafka is integrated with the external world). 
In this small project various error situations should be tested using the JDBC sink connector. The DLQ functionality should be included in the tests.
A test with a valid message, a wrongly formatted message will be done, as well as a test where  a constraint on the database-side will cause the operation to fail. 

## Requirements

### Must

1. A valid record should be written to the database
2. A wrongly formatted message (not valid JSON) should be handled 
3. An error on the sink side (constraint error on the database side) should be handled

 
## References

1. [KIP-298: Error Handling in Connect](https://cwiki.apache.org/confluence/display/KAFKA/KIP-298%3A+Error+Handling+in+Connect)
2. [KAFKA-6738: Kafka Connect handling of bad data](https://issues.apache.org/jira/browse/KAFKA-6738)

## Implementation

See [here](./impl/README.md) for the implementation.
