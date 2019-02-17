from confluent_kafka import Consumer, KafkaError

c = Consumer({
    'bootstrap.servers': 'localhost:9092,localhost:9093',
    'group.id': 'dlq-consumer',
    'default.topic.config': {
        'auto.offset.reset': 'earliest'
    }
})

c.subscribe(['dlq'])

while True:
    msg = c.poll(1.0)

    if msg is None:
        continue
    if msg.error():
        if msg.error().code() == KafkaError._PARTITION_EOF:
            continue
        else:
            print(msg.error())
            break

    print('Received message: {}'.format(msg.value().decode('utf-8')))
    
    print('Headers of message:')
    for header in msg.headers():
      print(header[0] +  " => " + header[1][0:120])
c.close()
