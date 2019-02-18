# Kafka Connect DLQ Feature Tests with JDBC Sink 

## Setup

Create a topic with `cleanup.policy` set to `compact`:

```
docker exec -ti broker-1 kafka-topics --create \
--zookeeper zookeeper-1:2181 --topic person \
--replication-factor 3 --partitions 3 \
--config cleanup.policy=compact
```

Create the JDBC connector:

```
curl -X "POST" "$DOCKER_HOST_IP:8083/connectors" \
     -H "Content-Type: application/json" \
     --data '{
  "name": "jdbc-sink",
  "config": {  
      "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
      "tasks.max": "2",
      "topics": "person",
      "connection.url": "jdbc:postgresql://db/sample?user=sample&password=sample",
      "auto.create": "true",
      "insert.mode":"upsert",
      "pk.fields":"id",
      "pk.mode":"record_value",      
      "key.converter":"org.apache.kafka.connect.storage.StringConverter",
      "key.converter.schemas.enable":"false",  
      "value.converter":"org.apache.kafka.connect.json.JsonConverter",
      "value.converter.schemas.enable":"true",
      "errors.retry.timeout": "30000",
      "errors.retry.delay.max.ms":"3000",
      "errors.tolerance": "all",
      "errors.log.enable": "true",
	   "errors.log.include.messages": "true",
	   "errors.deadletterqueue.topic.name": "dlq",
	   "errors.deadletterqueue.context.headers.enable" : "true"      
    }
}'
```

Now let's check the status of the connector just created above:

```
curl -X GET -s http://$DOCKER_HOST_IP:8083/connectors/jdbc-sink/status | jq
```

You should see an output similar to the one below:

```
{
  "name": "jdbc-sink",
  "connector": {
    "state": "RUNNING",
    "worker_id": "connect-1:8083"
  },
  "tasks": [
    {
      "state": "RUNNING",
      "id": 0,
      "worker_id": "connect-1:8083"
    },
    {
      "state": "RUNNING",
      "id": 1,
      "worker_id": "connect-1:8083"
    }
  ],
  "type": "sink"
}
```

You can also use [Kafka Connect UI](http://streamingplatform:8003/) to monitor the connectors. 

![Alt Image Text](./images/kafka-connect-ui.png "Kafka Connect UI")

## Creating a few correct messages

Now with the connector in place, let's send some correct messages to the `person` topic, the JDBC Sink connector is listening to. But before doing that, let's listen on the log of the Kafka Connect `connect-1` container:

```
docker logs -f connect-1
```

We will be using the [`kafkacat`](https://docs.confluent.io/current/app-development/kafkacat-usage.html) utility to produce message, as it can be installed outside of the Kafka software distribution. We have to produce a key as the topic is log-compacted. 


```
echo '1,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":1,"name":"Paul"}}' | \
kafkacat -P -b localhost -t person -Z -K,
```

A message should get sent to Kafka and immediately being picked-up by the JDBC sink, which places it into a table also called `person` by default.

To check the database, we can also use the `psql` utility from within the `db` container:

```
docker exec -ti db psql -d sample -U sample -c "select * from person"
```

One row should be returned, showing the one record being added:

```
 name  | id 
-------+----
 Paul  |  1
(1 row)
```

Alternatively you can also use the Adminer utility (<http://streamingplatform:38080>) started with the stack to bvrowse the database. 

You can also see the INSERT statement in the `connect-1` log.

```
[2019-02-16 10:38:33,469] INFO Closing BufferedRecords with preparedStatement: INSERT INTO "person" ("id","name") VALUES (1,'Paul') ON CONFLICT ("id") DO UPDATE SET "name"=EXCLUDED."name" (io.confluent.connect.jdbc.sink.BufferedRecords)
```

Now let's create another message

```
echo '2,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":2,"name":"Gaby"}}' | \
kafkacat -P -b localhost -t person -Z -K,
```

and check that it is placed into the person table as well.

```
docker exec -ti db psql -d sample -U sample -c "select * from person"
```

So we have successfully tested the "happy" flow. But what happens in case of "poisonous" messages? Let's first test the DLQ beaviour by sending a badly formated message. 

## Testing DLQ behaviour

In the connector we have also specified the error behaviour. Here are the relevant settings from the JDBC Sink connect configuration:

```
"errors.retry.timeout": "30000",
"errors.retry.delay.max.ms":"3000",
"errors.tolerance": "all",
"errors.log.enable": "true",
"errors.log.include.messages": "true",
"errors.deadletterqueue.topic.name": "dlq",
"errors.deadletterqueue.context.headers.enable" : "true"
```

Let's test it with a badly formatted message. 

First let's watch the log file of the `connect-1` container.

```
docker logs -f connect-1
```

Now start a kafkact consumer on the `dlq` topic 

```
kafkacat -b ${DOCKER_HOST_IP} -t dlq
```

Next produce the badly formatted message using kafakcat

```
echo '3,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":3,"name":"Invalid"}' | \
kafkacat -P -b localhost -t person -Z -K,
```

This message is missing an closing curly braces at the end and is therefore not a valid JSON message. 

You should see exceptions in the log similar as the one shown below ...

```
[2019-02-16 10:45:56,831] ERROR Error encountered in task jdbc-sink-0. Executing stage 'VALUE_CONVERTER' with class 'org.apache.kafka.connect.json.JsonConverter', where consumed record is {topic='person', partition=0, offset=1, timestamp=1550313955799, timestampType=CreateTime}. (org.apache.kafka.connect.runtime.errors.LogReporter)
org.apache.kafka.connect.errors.DataException: Converting byte[] to Kafka Connect data failed due to serialization error: 
	at org.apache.kafka.connect.json.JsonConverter.toConnectData(JsonConverter.java:334)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.lambda$convertAndTransformRecord$1(WorkerSinkTask.java:514)
	at org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator.execAndRetry(RetryWithToleranceOperator.java:128)
	at org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator.execAndHandleError(RetryWithToleranceOperator.java:162)
	at org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator.execute(RetryWithToleranceOperator.java:104)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.convertAndTransformRecord(WorkerSinkTask.java:514)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.convertMessages(WorkerSinkTask.java:491)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.poll(WorkerSinkTask.java:322)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.iteration(WorkerSinkTask.java:226)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.execute(WorkerSinkTask.java:194)
	at org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:175)
	at org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:219)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
Caused by: org.apache.kafka.common.errors.SerializationException: com.fasterxml.jackson.core.io.JsonEOFException: Unexpected end-of-input: expected close marker for Object (start marker at [Source: (byte[])"{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":5,"name":"Invalid"}"; line: 1, column: 1])
 at [Source: (byte[])"{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":5,"name":"Invalid"}"; line: 1, column: 413]
Caused by: com.fasterxml.jackson.core.io.JsonEOFException: Unexpected end-of-input: expected close marker for Object (start marker at [Source: (byte[])"{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":3,"name":"Invalid"}"; line: 1, column: 1])
 at [Source: (byte[])"{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":5,"name":"Invalid"}"; line: 1, column: 413]
	at com.fasterxml.jackson.core.base.ParserMinimalBase._reportInvalidEOF(ParserMinimalBase.java:594)
	at com.fasterxml.jackson.core.base.ParserBase._handleEOF(ParserBase.java:485)
	at com.fasterxml.jackson.core.base.ParserBase._eofAsNextChar(ParserBase.java:497)
	at com.fasterxml.jackson.core.json.UTF8StreamJsonParser._skipWSOrEnd(UTF8StreamJsonParser.java:2925)
	at com.fasterxml.jackson.core.json.UTF8StreamJsonParser.nextFieldName(UTF8StreamJsonParser.java:956)
	at com.fasterxml.jackson.databind.deser.std.BaseNodeDeserializer.deserializeObject(JsonNodeDeserializer.java:247)
	at com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer.deserialize(JsonNodeDeserializer.java:68)
	at com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer.deserialize(JsonNodeDeserializer.java:15)
	at com.fasterxml.jackson.databind.ObjectMapper._readTreeAndClose(ObjectMapper.java:4056)
	at com.fasterxml.jackson.databind.ObjectMapper.readTree(ObjectMapper.java:2571)
	at org.apache.kafka.connect.json.JsonDeserializer.deserialize(JsonDeserializer.java:50)
	at org.apache.kafka.connect.json.JsonConverter.toConnectData(JsonConverter.java:332)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.lambda$convertAndTransformRecord$1(WorkerSinkTask.java:514)
	at org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator.execAndRetry(RetryWithToleranceOperator.java:128)
	at org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator.execAndHandleError(RetryWithToleranceOperator.java:162)
	at org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator.execute(RetryWithToleranceOperator.java:104)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.convertAndTransformRecord(WorkerSinkTask.java:514)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.convertMessages(WorkerSinkTask.java:491)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.poll(WorkerSinkTask.java:322)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.iteration(WorkerSinkTask.java:226)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.execute(WorkerSinkTask.java:194)
	at org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:175)
	at org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:219)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
```

... as well as the faulty message should show up in the `dlq` topic. 

In contrast to the original message, the `dlq` message contains a set of header properties, which document the cause of the error (all starting with `__connect.errors`. 

Unfortunately it is not possible to visualize them using the kafka console consumer utilities. You can either use the [Kafka Tool](http://www.kafkatool.com/) to look into the message:

![Alt Image Text](./images/kafka-tool-show-headers.png "Kafka Tool Show Headers")

Or you can write your own Kafka client programm to do the job. For example using the [consume-dlq](consume-dlq.py) python script

```
python consume-dlq.py
```
you will see the headers as shown below:

```
Received message: {"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":3,"name":"Invalid"}
Headers of message:
__connect.errors.topic => person
__connect.errors.partition => 0
__connect.errors.offset => 9
__connect.errors.connector.name => jdbc-sink
__connect.errors.task.id => 0
__connect.errors.stage => VALUE_CONVERTER
__connect.errors.class.name => org.apache.kafka.connect.json.JsonConverter
__connect.errors.exception.class.name => org.apache.kafka.connect.errors.DataException
__connect.errors.exception.message => Converting byte[] to Kafka Connect data failed due to serialization error: 
__connect.errors.exception.stacktrace => org.apache.kafka.connect.errors.DataException: Converting byte[] to Kafka Connect data failed due to serialization error
```

Next if you send a correct message it will get through normally

```
echo '4,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":4,"name":"Peter"}}' | \
kafkacat -P -b localhost -t person -Z -K,
```

The poisonous message from before is not causing any side-effects.

We have seen how the DLQ feature can help to forward messages a connector can not handle to a "dead-letter" topic. 

But what about errors on the sink side, i.e. in our case caused when trying to INSERT/UPDATE the data on the database side. 

## Testing a message with a value too large for name

In order to force an error on the database, we have to change the datatype of the `name` column to `character varying(10)` to restrict the column size to 10 only. 

```
docker exec -ti db psql -d sample -U sample -c "ALTER TABLE person ALTER COLUMN name TYPE VARCHAR(10);"
```

So if we now use a message with a name length longer than 10, we will force an error on the database. 

Send the following message through the console producer

```
echo '5,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":5,"name":"Michael Mueller"}}' | \
kafkacat -P -b localhost -t person -Z -K,
```

You will see multiple errors in the log of the `connect-1` container due to retriess, similar to the one below. 

```
[2019-02-16 12:42:29,312] ERROR WorkerSinkTask{id=jdbc-sink-0} Task threw an uncaught and unrecoverable exception (org.apache.kafka.connect.runtime.WorkerTask)
org.apache.kafka.connect.errors.ConnectException: Exiting WorkerSinkTask due to unrecoverable exception.
	at org.apache.kafka.connect.runtime.WorkerSinkTask.deliverMessages(WorkerSinkTask.java:587)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.poll(WorkerSinkTask.java:323)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.iteration(WorkerSinkTask.java:226)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.execute(WorkerSinkTask.java:194)
	at org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:175)
	at org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:219)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
Caused by: org.apache.kafka.connect.errors.ConnectException: java.sql.SQLException: java.sql.BatchUpdateException: Batch entry 0 INSERT INTO "person" ("id","name") VALUES (5,'Michael Mueller') ON CONFLICT ("id") DO UPDATE SET "name"=EXCLUDED."name" was aborted.  Call getNextException to see the cause.
org.postgresql.util.PSQLException: ERROR: value too long for type character varying(10)

	at io.confluent.connect.jdbc.sink.JdbcSinkTask.put(JdbcSinkTask.java:86)
	at org.apache.kafka.connect.runtime.WorkerSinkTask.deliverMessages(WorkerSinkTask.java:565)
	... 10 more
Caused by: java.sql.SQLException: java.sql.BatchUpdateException: Batch entry 0 INSERT INTO "person" ("id","name") VALUES (5,'Michael Mueller') ON CONFLICT ("id") DO UPDATE SET "name"=EXCLUDED."name" was aborted.  Call getNextException to see the cause.
org.postgresql.util.PSQLException: ERROR: value too long for type character varying(10)

	... 12 more
```

after a few retries, the `jdbc-sink` will get killed and has to be manually restarted

```	
[2019-02-16 12:42:29,313] ERROR WorkerSinkTask{id=jdbc-sink-0} Task is being killed and will not recover until manually restarted (org.apache.kafka.connect.runtime.WorkerTask)
[2019-02-16 12:42:29,313] INFO Stopping task (io.confluent.connect.jdbc.sink.JdbcSinkTask)
[2019-02-16 12:42:29,313] INFO Closing connection #1 to PostgreSql (io.confluent.connect.jdbc.util.CachedConnectionProvider)
[2019-02-16 12:42:29,318] INFO [Consumer clientId=consumer-7, groupId=connect-jdbc-sink] Sending LeaveGroup request to coordinator 192.168.73.85:9092 (id: 2147483646 rack: null) (org.apache.kafka.clients.consumer.internals.AbstractCoordinator)
[2019-02-16 12:42:29,974] INFO 172.19.0.1 - - [16/Feb/2019:12:42:29 +0000] "GET / HTTP/1.1" 200 95  1 (org.apache.kafka.connect.runtime.rest.RestServer)
[2019-02-16 12:42:29,979] INFO 172.19.0.1 - - [16/Feb/2019:12:42:29 +0000] "GET /connectors HTTP/1.1" 200 13  1 (org.apache.kafka.connect.runtime.rest.RestServer)
```

you can check the status of the connector through the REST API

```
curl -X GET -s http://$DOCKER_HOST_IP:8083/connectors/jdbc-sink/status | jq
```

and you can see that all tasks are in `FAILED` state due to the same unrecoverable exception, due to the poisonous message with the value too large for the name column.

```
{
  "name": "jdbc-sink",
  "connector": {
    "state": "RUNNING",
    "worker_id": "connect-1:8083"
  },
  "tasks": [
    {
      "state": "FAILED",
      "trace": "org.apache.kafka.connect.errors.ConnectException: Exiting WorkerSinkTask due to unrecoverable exception.\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.deliverMessages(WorkerSinkTask.java:587)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.poll(WorkerSinkTask.java:323)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.iteration(WorkerSinkTask.java:226)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.execute(WorkerSinkTask.java:194)\n\tat org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:175)\n\tat org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:219)\n\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n\tat java.util.concurrent.FutureTask.run(FutureTask.java:266)\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n\tat java.lang.Thread.run(Thread.java:748)\nCaused by: org.apache.kafka.connect.errors.ConnectException: java.sql.SQLException: java.sql.BatchUpdateException: Batch entry 0 INSERT INTO \"person\" (\"id\",\"name\") VALUES (5,'Michael Mueller') ON CONFLICT (\"id\") DO UPDATE SET \"name\"=EXCLUDED.\"name\" was aborted.  Call getNextException to see the cause.\norg.postgresql.util.PSQLException: ERROR: value too long for type character varying(10)\n\n\tat io.confluent.connect.jdbc.sink.JdbcSinkTask.put(JdbcSinkTask.java:86)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.deliverMessages(WorkerSinkTask.java:565)\n\t... 10 more\nCaused by: java.sql.SQLException: java.sql.BatchUpdateException: Batch entry 0 INSERT INTO \"person\" (\"id\",\"name\") VALUES (5,'Michael Mueller') ON CONFLICT (\"id\") DO UPDATE SET \"name\"=EXCLUDED.\"name\" was aborted.  Call getNextException to see the cause.\norg.postgresql.util.PSQLException: ERROR: value too long for type character varying(10)\n\n\t... 12 more\n",
      "id": 0,
      "worker_id": "connect-1:8083"
    },
    {
      "state": "FAILED",
      "trace": "org.apache.kafka.connect.errors.ConnectException: Exiting WorkerSinkTask due to unrecoverable exception.\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.deliverMessages(WorkerSinkTask.java:587)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.poll(WorkerSinkTask.java:323)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.iteration(WorkerSinkTask.java:226)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.execute(WorkerSinkTask.java:194)\n\tat org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:175)\n\tat org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:219)\n\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n\tat java.util.concurrent.FutureTask.run(FutureTask.java:266)\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n\tat java.lang.Thread.run(Thread.java:748)\nCaused by: org.apache.kafka.connect.errors.ConnectException: java.sql.SQLException: java.sql.BatchUpdateException: Batch entry 0 INSERT INTO \"person\" (\"id\",\"name\") VALUES (5,'Michael Mueller') ON CONFLICT (\"id\") DO UPDATE SET \"name\"=EXCLUDED.\"name\" was aborted.  Call getNextException to see the cause.\norg.postgresql.util.PSQLException: ERROR: value too long for type character varying(10)\n\n\tat io.confluent.connect.jdbc.sink.JdbcSinkTask.put(JdbcSinkTask.java:86)\n\tat org.apache.kafka.connect.runtime.WorkerSinkTask.deliverMessages(WorkerSinkTask.java:565)\n\t... 10 more\nCaused by: java.sql.SQLException: java.sql.BatchUpdateException: Batch entry 0 INSERT INTO \"person\" (\"id\",\"name\") VALUES (5,'Michael Mueller') ON CONFLICT (\"id\") DO UPDATE SET \"name\"=EXCLUDED.\"name\" was aborted.  Call getNextException to see the cause.\norg.postgresql.util.PSQLException: ERROR: value too long for type character varying(10)\n\n\t... 12 more\n",
      "id": 1,
      "worker_id": "connect-1:8083"
    }
  ],
  "type": "sink"
}
```

If we send a new correct message, it will of course not get processed, due all the taks being in `FAILED` state.

```
echo '6,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":6,"name":"Barbara"}}' | \
kafkacat -P -b localhost -t person -Z -K,
```

A restart of the connector will not help:

```
curl -X POST -s http://$DOCKER_HOST_IP:8083/connectors/jdbc-sink/restart
```

We can see that the DLQ feature is not working in that case. There is a thread on Confluent Slack describing a similar problem: <https://confluentcommunity.slack.com/archives/C49L0V3L7/p1542128005234400>. 
The answer from Arjun Satish clarifies the behaviour: *"hey Kelly, problems with the connector itself cannot be handled by the DLQ. this is because when the connector throws an (non-retriable) exception, we don’t know on which SinkRecord the failure occurred"*


So how can we fix the poisonous message so that it will not get stuck again. Due to the fact that the person topic is a log compacted topic, we can send a correction message, and then force the compaction to take palce, before we restart the connector. 

Let's produce the corrected message (value only "Michael" and therefore fitting the name column).

```
echo '5,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":5,"name":"Michael"}}' | \
kafkacat -P -b localhost -t person -Z -K,
```

Connect to the `broker-1` container

```
docker exec -ti broker-1 bash
```

and perform an `ls` on `/var/lib/kafka/data/person-*`

```
root@9f596f79a66e:/# ls -lsa /var/lib/kafka/data/person-*
/var/lib/kafka/data/person-0:
total 12
4 drwxr-xr-x  2 root root     4096 Feb 17 20:48 .
4 drwxrwxrwx 39 root root     4096 Feb 17 20:51 ..
0 -rw-r--r--  1 root root 10485760 Feb 17 20:48 00000000000000000000.index
0 -rw-r--r--  1 root root        0 Feb 17 20:48 00000000000000000000.log
0 -rw-r--r--  1 root root 10485756 Feb 17 20:48 00000000000000000000.timeindex
4 -rw-r--r--  1 root root        8 Feb 17 20:48 leader-epoch-checkpoint

/var/lib/kafka/data/person-1:
total 16
4 drwxr-xr-x  2 root root     4096 Feb 17 20:48 .
4 drwxrwxrwx 39 root root     4096 Feb 17 20:51 ..
0 -rw-r--r--  1 root root 10485760 Feb 17 20:48 00000000000000000000.index
4 -rw-r--r--  1 root root     1670 Feb 17 20:50 00000000000000000000.log
0 -rw-r--r--  1 root root 10485756 Feb 17 20:48 00000000000000000000.timeindex
4 -rw-r--r--  1 root root        8 Feb 17 20:48 leader-epoch-checkpoint

/var/lib/kafka/data/person-2:
total 16
4 drwxr-xr-x  2 root root     4096 Feb 17 20:48 .
4 drwxrwxrwx 39 root root     4096 Feb 17 20:51 ..
0 -rw-r--r--  1 root root 10485760 Feb 17 20:48 00000000000000000000.index
4 -rw-r--r--  1 root root      275 Feb 17 20:48 00000000000000000000.log
0 -rw-r--r--  1 root root 10485756 Feb 17 20:48 00000000000000000000.timeindex
4 -rw-r--r--  1 root root        8 Feb 17 20:48 leader-epoch-checkpoint
```

We can see that there is one log segment per partition for the topic `person`.

Now let's change the `segement.ms` to 1 second, so that it is closed after 1 second, independent of the size of the segment.
You can do that using the `kafka-configs` cli within the docker container.

```
docker exec -ti broker-1 kafka-configs --zookeeper zookeeper-1:2181 --alter --entity-name person --entity-type topics --add-config segment.ms=1000
```

We also set the `min.cleanable.dirty.ratio` to `0.01` to force the log compaction process to run. 

```
docker exec -ti broker-1 kafka-configs --zookeeper zookeeper-1:2181 --alter --entity-name person --entity-type topics --add-config min.cleanable.dirty.ratio=0.01
```

If there are no other messages being produce, just produce the correction message again to force the rollover to a new segment. 

```
echo '5,{"schema":{"type":"struct","fields":[{"type":"int16","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"}],"optional":false,"name":"msgschema"},"payload":{"id":5,"name":"Michael"}}' | \
kafkacat -P -b localhost -t person -Z -K,
```

Now let's see the result of both the rollover to a new segment and the compaction. 

```
docker exec -ti broker-1 bash
```

and perform an `ls` on `/var/lib/kafka/data/person-*`

```
root@9f596f79a66e:/# ls -lsa /var/lib/kafka/data/person-*
/var/lib/kafka/data/person-0:
total 12
4 drwxr-xr-x  2 root root     4096 Feb 17 20:48 .
4 drwxrwxrwx 39 root root     4096 Feb 17 20:57 ..
0 -rw-r--r--  1 root root 10485760 Feb 17 20:48 00000000000000000000.index
0 -rw-r--r--  1 root root        0 Feb 17 20:48 00000000000000000000.log
0 -rw-r--r--  1 root root 10485756 Feb 17 20:48 00000000000000000000.timeindex
4 -rw-r--r--  1 root root        8 Feb 17 20:48 leader-epoch-checkpoint

/var/lib/kafka/data/person-1:
total 28
4 drwxr-xr-x  2 root root     4096 Feb 17 20:55 .
4 drwxrwxrwx 39 root root     4096 Feb 17 20:57 ..
0 -rw-r--r--  1 root root        0 Feb 17 20:50 00000000000000000000.index
4 -rw-r--r--  1 root root     1384 Feb 17 20:50 00000000000000000000.log
4 -rw-r--r--  1 root root       12 Feb 17 20:50 00000000000000000000.timeindex
0 -rw-r--r--  1 root root 10485760 Feb 17 20:54 00000000000000000006.index
4 -rw-r--r--  1 root root      278 Feb 17 20:54 00000000000000000006.log
4 -rw-r--r--  1 root root       10 Feb 17 20:54 00000000000000000006.snapshot
0 -rw-r--r--  1 root root 10485756 Feb 17 20:54 00000000000000000006.timeindex
4 -rw-r--r--  1 root root        8 Feb 17 20:48 leader-epoch-checkpoint

/var/lib/kafka/data/person-2:
total 16
4 drwxr-xr-x  2 root root     4096 Feb 17 20:48 .
4 drwxrwxrwx 39 root root     4096 Feb 17 20:57 ..
0 -rw-r--r--  1 root root 10485760 Feb 17 20:48 00000000000000000000.index
4 -rw-r--r--  1 root root      275 Feb 17 20:48 00000000000000000000.log
0 -rw-r--r--  1 root root 10485756 Feb 17 20:48 00000000000000000000.timeindex
4 -rw-r--r--  1 root root        8 Feb 17 20:48 leader-epoch-checkpoint
```

Let's delete the two config settings so that they are set back to the default

```
docker exec -ti broker-1 kafka-configs --zookeeper zookeeper-1:2181 --alter --entity-name person --entity-type topics --delete-config segment.ms

docker exec -ti broker-1 kafka-configs --zookeeper zookeeper-1:2181 --alter --entity-name person --entity-type topics --delete-config min.cleanable.dirty.ratio
```

With the correction record in place, we should be able to start the connector again. First delete the failed instance

```
curl -X "DELETE" "$DOCKER_HOST_IP:8083/connectors/jdbc-sink"
```

and then recreate it

```
curl -X "POST" "$DOCKER_HOST_IP:8083/connectors" \
     -H "Content-Type: application/json" \
     --data '{
  "name": "jdbc-sink",
  "config": {  
      "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
      "tasks.max": "2",
      "topics": "person",
      "connection.url": "jdbc:postgresql://db/sample?user=sample&password=sample",
      "auto.create": "true",
      "insert.mode":"upsert",
      "pk.fields":"id",
      "pk.mode":"record_value",      
      "key.converter":"org.apache.kafka.connect.storage.StringConverter",
      "key.converter.schemas.enable":"false",  
      "value.converter":"org.apache.kafka.connect.json.JsonConverter",
      "value.converter.schemas.enable":"true",
      "errors.retry.timeout": "30000",
      "errors.retry.delay.max.ms":"3000",
      "errors.tolerance": "all",
      "errors.log.enable": "true",
	   "errors.log.include.messages": "true",
	   "errors.deadletterqueue.topic.name": "dlq",
	   "errors.deadletterqueue.context.headers.enable" : "true"      
    }
}'
```

Check the status of the connector and validate that it is again running

```
curl -X GET -s http://$DOCKER_HOST_IP:8083/connectors/jdbc-sink/status | jq
```

Let's see that the data for record 5 (correction) and 6 has been inserted into the `person` table. 

```
docker exec -ti db psql -d sample -U sample -c "select * from person"
```








