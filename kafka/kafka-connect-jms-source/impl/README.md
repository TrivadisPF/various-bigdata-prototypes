# Kafka Connect with JMS Source

## Setup  

Download connect from <https://github.com/Landoop/stream-reactor/releases> and copy it into the `kafka-connect` folder

```
wget https://github.com/Landoop/stream-reactor/releases/download/1.2.1/kafka-connect-jms-1.2.1-2.1.0-all.tar.gz
mkdir kafka-connect-jms-1.2.1-2.1.0
tar -zxvf kafka-connect-jms-1.2.1-2.1.0-all.tar.gz -C kafka-connect-jms-1.2.1-2.1.0
rm kafka-connect-jms-1.2.1-2.1.0-all.tar.gz
```

### ActiveMQ

```
wget https://repo1.maven.org/maven2/org/apache/activemq/activemq-all/5.15.2/activemq-all-5.15.2.jar -o kafka-connect-jms-1.2.1-2.1.0/activemq-all-5.15.2.jar 
```


```
curl -X "POST" "$DOCKER_HOST_IP:8083/connectors" \
     -H "Content-Type: application/json" \
     --data '{
    "name": "jms-source",
    "config": {
        "connector.class": "com.datamountaineer.streamreactor.connect.jms.source.JMSSourceConnector",
        "connect.jms.initial.context.factory": "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
        "tasks.max": "1",
        "connect.jms.connection.factory": "ConnectionFactory",
        "connect.jms.url": "tcp://activemq:61616",
        "connect.jms.kcql": "INSERT INTO jms-queue SELECT * FROM jms_queue WITHTYPE QUEUE"
    }
}'
```


```
curl -X "GET" "$DOCKER_HOST_IP:8083/connectors/jms-source/status" | jq
```

```
{
  "name": "jms-source",
  "connector": {
    "state": "RUNNING",
    "worker_id": "connect-2:8084"
  },
  "tasks": [
    {
      "state": "RUNNING",
      "id": 0,
      "worker_id": "connect-1:8083"
    }
  ],
  "type": "source"
}
```

<http://analyticsplatform:28002>


This is the default implementation. The payload is taken as is: an array of bytes and sent over Kafka as an AVRO record with Schema.BYTES. You don’t have to provide a mapping for the source to get this converter!!

```
{
  "type": "record",
  "name": "jms",
  "namespace": "com.datamountaineer.streamreactor.connect",
  "fields": [
    {
      "name": "message_timestamp",
      "type": [
        "null",
        "long"
      ],
      "default": null
    },
    {
      "name": "correlation_id",
      "type": [
        "null",
        "string"
      ],
      "default": null
    },
    {
      "name": "redelivered",
      "type": [
        "null",
        "boolean"
      ],
      "default": null
    },
    {
      "name": "reply_to",
      "type": [
        "null",
        "string"
      ],
      "default": null
    },
    {
      "name": "destination",
      "type": [
        "null",
        "string"
      ],
      "default": null
    },
    {
      "name": "message_id",
      "type": [
        "null",
        "string"
      ],
      "default": null
    },
    {
      "name": "mode",
      "type": [
        "null",
        "int"
      ],
      "default": null
    },
    {
      "name": "type",
      "type": [
        "null",
        "string"
      ],
      "default": null
    },
    {
      "name": "priority",
      "type": [
        "null",
        "int"
      ],
      "default": null
    },
    {
      "name": "bytes_payload",
      "type": [
        "null",
        "bytes"
      ],
      "default": null
    },
    {
      "name": "properties",
      "type": [
        "null",
        {
          "type": "map",
          "values": "string"
        }
      ],
      "default": null
    }
  ],
  "connect.name": "com.datamountaineer.streamreactor.connect.jms"
}
```


# Running on AWS Lightstail

```
# Install Docker and Docker Compose
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable edge"
apt-get install -y docker-ce
curl -L "https://github.com/docker/compose/releases/download/1.23.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# Install wget
apt-get install -y wget

# Prepare Environment
export PUBLIC_IP=$(curl ipinfo.io/ip)
export DOCKER_HOST_IP=$(ip addr show eth0 | grep "inet\b" | awk '{print $2}' | cut -d/ -f1)
git clone https://github.com/TrivadisBDS/modern-data-analytics-stack.git
cd modern-data-analytics-stack/docker
mkdir kafka-connect

# Startup Environment
docker-compose up
```


