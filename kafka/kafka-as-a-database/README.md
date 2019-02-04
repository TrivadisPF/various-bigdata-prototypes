# "Kafka as a Database"
This project will look at the different solutions for accessing a Kafka topic using SQL. In contrast to KSQL, the idea is **not** to use SQL on the datastream (unbounded and near real-time) but to scan the complete topic (historic) data in a bounded fashion.  

## Context

Situations we **need** to solve with this project:

* all the data of a given Kafka topic can be queried using SQL
* data of multiple Kafka topic can be joined using SQL
* data of a Kafka can be joined with data from another data store
* the messages are serialized as either Avro (backed by schema registry), JSON, CSV

Situations we **do not have** to solve with this project:

* querying in an unbounded fashion, i.e. Streaming SQL => this is solved by KSQL

## High-Level overview

If we have Kafka topics with a certain history (either because we don't delete at all, the data retention is set to large-enough value or we have a compacted topic), then it could be interesting to query that data in a an-hoc and flexible manner. 

There are many use cases for that, a few ones are listed below:

* SQL on Kafka can help to position Kafka as an event store in an event sourcing case
* Use Batch SQL on Kafka to find out if a given situation/pattern can be detected in the events history and later move the logic to KSQL for real time execution

We could also use KSQL and always start with offset 0 (at the beginning of the topic) and achive a similar result, but the approach taken here also allows for joining with data sitting outside of the Kafka.

## Requirements

### Must-Have

1. a topic should be queriable using "standard" SQL
1. a table should map to a topic with messages formatted either as Avro, JSON, CSV 
2. measure performance of a full topic scan without restriction

### Optional
 
1. when querying a topic, it should be possible to restrict on one or more partitions

## References

1. Starburst Data (Presto): <https://www.starburstdata.com>
2. Presto: <https://prestosql.io/docs/current/connector/kafka.html>
3. Apache Drill: <https://drill.apache.org/docs/kafka-storage-plugin/> 



## Implementation
See [here](./impl/README.md) for the implementation.
