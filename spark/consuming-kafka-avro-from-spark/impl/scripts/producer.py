from confluent_kafka import avro
from confluent_kafka.avro import AvroProducer
import time
import sys

def main(argv):

	brokers = argv[0] 
	schemaRegistryUrl = argv[1] 
	timestamp = argv[2] 
	id = argv[3] 
	firstName = argv[4]
	lastName = argv[5]
	
	value_schema_str = """
	{
	   "namespace": "my.test",
	   "name": "Person",
	   "type": "record",
	   "fields" : [
		 {
		   "name" : "id",
		   "type" : "int"
		 },
		 {
		   "name" : "firstName",
		   "type" : "string"
		 },
		 {
		   "name" : "lastName",
		   "type" : "string"
		 }
	   ]
	}
	"""

	key_schema_str = """
	{
	   "namespace": "my.test",
	   "name": "PersonKey",
	   "type": "record",
	   "fields" : [
		 {
		   "name" : "id",
		   "type" : "string"
		 }
	   ]
	}
	"""

	value_schema = avro.loads(value_schema_str)
	key_schema = avro.loads(key_schema_str)
	value = {"id":int(id), "firstName": firstName, "lastName": lastName}
	key = {"id": id}

	avroProducer = AvroProducer({
		'bootstrap.servers': brokers,
		'schema.registry.url': schemaRegistryUrl,
		'compression.codec': 'snappy'
		}, default_key_schema=key_schema, default_value_schema=value_schema)

	avroProducer.produce(topic='person-v1', value=value, key=key, timestamp=int(timestamp))
	avroProducer.flush()
	
if __name__ == "__main__":
   main(sys.argv[1:])	
