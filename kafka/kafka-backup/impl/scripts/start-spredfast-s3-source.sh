#!/bin/bash

echo "creating Spreadfast S3 Source Connector"

curl -X "POST" "$DOCKER_HOST_IP:8083/connectors" \
     -H "Content-Type: application/json" \
     --data '{
  "name": "s3-spredfast-source",
  "config": {
      "connector.class": "com.spredfast.kafka.connect.s3.source.S3SourceConnector",
      "tasks.max": "1",
      "s3.bucket": "gschmutz-kafka-spredfast-1",
      "s3.prefix": "test",
      "s3.start.marker": "2019-04-23",
      "s3.page.size": "100"
      "topics": "truck_position",
      "targetTopic.truck_position": "truck_position"
      "max.partition.count": "200",
	    "key.converter": "com.spredfast.kafka.connect.s3.AlreadyBytesConverter",
      "value.converter": "com.spredfast.kafka.connect.s3.AlreadyBytesConverter",
      "format": "binary",
      "format.include.keys": "true",
      "max.poll.records": "1000",
  }
}'