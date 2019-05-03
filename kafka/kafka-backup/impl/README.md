# Backup of Kafka Data

This project documents the implementation of the backup of Kafka topics.

## Implementation Decisions

  * Kafka Connect to be used for the restore as well
    * this will automatically help in parallelization of the restore
  * Kafka Connect should be used for the backup 
    * this is the most natural choice in the Kafka ecosystem for constantly writing out data from topics
  * there are two possible Kafka Connectors available for use
    * Confluent S3
      * supported by Confluent
      * open source
      * only implements a Writer
      * supports exactly once delivery
      * pluggable partitioner (out-of-the-box Partition Based or Time Based)
      * does allow binary data, but does not support Offset, Timestamp, Headers  
    * Spredfast S3 
      * both Writer and Reader implementation
      *       