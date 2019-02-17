# Ideas

## Real Data Sources

* Implement Twitter Sensor using Spring Boot
  * Dockerize it so that it can be configured through environment variables
  * based on work done at Armasuisse W&T
  * What would be the advantage compared of using Kafka Connect?

* Implementing a Connector to IEX Group Trading data
  * Trading data available from <https://iextrading.com/developer/>
  * Java API: <https://github.com/WojciechZankowski/iextrading4j>

* Working with OpenSky Flight Data (<https://opensky-network.org/>, <https://opensky-network.org/network/explorer>)
  * Public API available: <https://opensky-network.org/apidoc/rest.html> 
  * Metadata: <https://opensky-network.org/datasets/metadata/>
  * Rest API: `curl -s "https://opensky-network.org/api/states/all" | python -m json.tool>`
    
## Data Source Simulator

* Simulating repeatable, time-based data to Kafka ([project](streaming-sources/stream-simulator/README.md))
  * Generating data to Kafka based on timestamp with different data types
  * using an Excel sheet as input, holding the input
  * possibly to merge multiple excel sheets by time
  * allow for fast forwarding and play with speed factor
  * maybe using Streamsets as the engine, by generating the flow and running headless

* Implement NYC Taxi Simulator
  * Data available here: <http://www.nyc.gov/html/tlc/html/about/trip_record_data.shtml>
  * Data Dictionary: <http://www.nyc.gov/html/tlc/downloads/pdf/data_dictionary_trip_records_yellow.pdf>
  * Flink example: <https://github.com/dataArtisans/flink-training-exercises/tree/master/src/main/java/com/dataartisans/flinktraining/exercises/datastream_java>
  * Video "Live data discovery from the NYC Taxi Trip Records stream: <https://www.youtube.com/watch?v=wlrvooFLlXo>
  * Google sample: <https://codelabs.developers.google.com/codelabs/cloud-dataflow-nyc-taxi-tycoon/#0>
  * Video "MemSQL Supercar Real-Time Geospatial Demo": <https://www.youtube.com/watch?v=2txICCLUV-Y>
  * Google Feeder: <https://github.com/GoogleCloudPlatform/nyc-taxirides-stream-feeder>
  
* Ford GoBike Trip Data
  * implement a connector
  * General Bikeshare Feed Specification: <https://github.com/NABSA/gbfs/blob/master/gbfs.md>
  * Article from DZone: <https://dzone.com/articles/building-a-real-time-bike-share-data-pipeline-with>

* Implement a click stream simulator
  * <https://github.com/mapr-demos/customer360/tree/master/clickstream> 

## Integrating RDMBS

* Play with Debezium
  * Implement a sample which is using CDC to consume changes from a RDBMS 
  * <https://debezium.io/> 	
  * Creating DDD aggregates with Debeziumg and Kafka Streams: <https://debezium.io/blog/2018/03/08/creating-ddd-aggregates-with-debezium-and-kafka-streams/>
 
* Investigate on bi-directional integration between Oracle RDBMS and Kafka
  * using Oracle AQ and JMS (Kafka Connect JMS)
  * using Oracle AQ and maybe custom Kafka Connect Connector     
    
## Data Sinks

* Working with DataStax Kafka Connector
  * Blog: <https://www.datastax.com/2018/12/introducing-the-datastax-apache-kafka-connector>
  * Documentation: <https://github.com/datastax/kafka-examples>
  * Samples: <https://github.com/datastax/kafka-examples>
  * Short Course: <https://academy.datastax.com/resources/getting-started-datastax-apache-kafka%E2%84%A2-connector>

* Integrating with Influx DB
  * using the Influx Connector to write timeseries data to Influx DB
  * InfluxDB course on Udemy: <https://www.udemy.com/influxdb-time-series-database/>
  * Possible Use Case: <https://www.influxdata.com/blog/monitoring-bitcoin-and-cryptocurrencies-with-influxdb-and-telegraf/>

* Working with Neo4J
  * Kafka Connect Neo4J: <https://www.confluent.io/connector/kafka-connect-neo4j/>
  * StreamSets and Neo4J: <https://neo4j.com/graphconnect-2018/session/ingesting-data-neo4j-mdm-streamsets>

* Implementing Kafka Connect and/or StreamSets Sink for DataStax Graph
  * based on idea from Armasuisse W&T project
  * store data as efficiently as possible in the graph
  * 1 message : many nodes 

* Streamsets with Snowflake
  * <https://streamsets.com/blog/data-collecting-for-snowflake/>

* Working with Druid
  * Try to implement a use case with Twitter data
  * either plain Druid (<http://druid.io/>) or using Imply platform (<https://imply.io/>)
  * Using Apache Superset for visualization (<https://superset.incubator.apache.org/>)

## Testing

* Testing and Kafka
  * with Zerocode: <https://github.com/authorjapps/zerocode>  
  * with Citrus Framework: <https://citrusframework.org/> 
 
## "Kafka as a Database" 

* Using Kafka as a Database ([project](kafka/kafka-as-a-database/README.md))
  * SQL to query from Kafka Topic
     * Starburst Data (Presto): <https://www.starburstdata.com>
     * Presto: <https://prestosql.io/docs/current/connector/kafka.html>
     * Apache Drill: <https://drill.apache.org/docs/kafka-storage-plugin/>
     * Dremio: <https://www.dremio.com/> - does not yet support Kafka, but it's on the roadmap
      
## Data Streaming

* End-to-End Sample with Timestamp and Custom Headers
  * Implement a sample which produces data from Kafka Client, StreamSets, Kafka Connect and Kafka Streams (KSQL) with setting the timestamp and custom headers (where possible)
  * Process the custom headers using the same tools 

* Implement Test with multiple message types per topic
  * <https://www.confluent.io/blog/put-several-event-types-kafka-topic/> 
  * <https://www.confluent.io/blog/put-several-event-types-kafka-topic/> 
  * Maven Plugin for Starting Kafka Broker: <https://github.com/arturmkrtchyan/kafka-maven-plugin>

## Devops

* Implement a Docker-Compose Stack Builder
  * Similar to Big Data Europe: <https://github.com/big-data-europe/app-stack-builder>, <https://cdn-images-1.medium.com/max/857/0*XqrlO-yBrWJhjzs9.png>

* Kafka and Continous Integration / Deployment
  * Maven Plugin for Kafka Topics: <https://github.com/Jean-Eudes/kafka-maven-plugin>

* Stream Data Integration and Continous Integration / Deployment
  * How can StreamSets integrate with CI / CD
  * How can Kafka Connect integrate with CI / CD 
     * starting a Kafka Connect Connector instance through a Docker container 
     * possibly wrap it inside a Spring Boot application
     * Kafka Connect Java REST API Client: <https://github.com/SourceLabOrg/kafka-connect-client>

* Kafka Topic Managment
  * Test [Kafkawize](https://github.com/kafkawize/kafkawize), a Self-Service Kafka Topic Management Portal and if useful add it to the streaming platform

## Management & Monitoring

* Kafka and Ansible

* (BigData) and Kubernetes
  * Use [Strimizi](https://github.com/strimzi/strimzi-kafka-operator), the RedHat Kafka Operator to setup a Kafka environment
  * Use Cassandra Operator

* Monitoring with Prometheus and Grafana 

* Try Kafka Cruise Control and LinkedIn Burrow
  * Github: <https://github.com/linkedin/cruise-control> 
  * [Introducing Kafka Cruise Control Frontend](https://engineering.linkedin.com/blog/2019/02/introducing-kafka-cruise-control-frontend)
  * Github: <https://github.com/linkedin/burrow> 

* Try Kafka Security Manager
  * Github: <https://github.com/simplesteph/kafka-security-manager>
  

## Potential Use Cases
* Twitter
* NYC Taxi
* Flight Tracker

## Assignments

* Beatrix
  * Kafka as a Database

* Michael
  * Sinks with Cassandra and Elasticsearch 

* Markus
  * Simulator auf Basis von CSV

* Dennis
  * Druid + Apache Superset

* Aron
  * Trading Source
  * Sink Cassandra (DataStax Cassandra)

* Markus 
  * Monitoring von Kafka (mit Michael Mühlbayer und Gergely)
  * Kafka and Ansible (Confluent)

* Yves
  * Sink InfluxDB    

     