# Sales Simulator

This project's highly configurable, Python-based, synthetic data generator (producer.py) streams product listings, sales transactions, and inventory restocking activities to Apache Kafka topics.

It is based on the excellent work of Gary Stafford available here: <https://github.com/garystafford/streaming-sales-generator>.

The following changes have been made to the original implementation:

 - configuration using environment variables
 - docker image mave available `trivadis/sales-simulator:latest`
 - publish Kafka message with key (`product_id`)
 - refactor to use the `confluent-kafka` python library 
 - support schema-based (avro) messages 

## Running the simulator

```bash
docker run -e KAFKA_BOOTSTRAP_SERVERS=localhost:9092 trivadis/sales-simulator:1.0.0
```

You can use the following environment variables to override default settings

| Varialbe  | Default  | Description  |
|-----------|-----------|-----------|
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Specify the endpoint of the Kafka Bootstrap Broker(s) |
| `KAFKA_SCHEMA_REGISTRY_URL` | `http://localhost:9092` | Specify the endpoint of the Schema Registry |
| `KAFKA_MESSAGE_FORMAT` | `json` | The message format to use when serializing the messages, either `json` or `avro`. |
| `KAFKA_TOPIC_PRODUCTS` | `demo.products` | Name of the Kafka topic holding product listings |
| `KAFKA_TOPIC_PURCHASES` | `demo.purchases` | Name of the Kafka topic holding sales transactions |
| `KAFKA_TOPIC_PURCHASES` | `demo.inventories` | Name of the Kafka topic holding restocking activities |

The `KAFKA_SCHEMA_REGISTRY_URL` has to be specified when  `KAFKA_MESSAGE_FORMAT` is set to `avro`.

## Running the simulator with custom configuration settings

if you want to change the configuration, create a `configuration.ini` with the default settings like shown below

```bash
[KAFKA]
# kafka boostrap servers provided as an environment variable
bootstrap_servers = $KAFKA_BOOTSTRAP_SERVERS

# schema registry url provided as an environment variable
schema_registry_url = $KAFKA_SCHEMA_REGISTRY_URL

# the message format to use for serializing the events, either "json, avro, json_schema (not yet supported)"
message_format = $KAFKA_MESSAGE_FORMAT

# kafka authentication method: plaintext or sasl_scram
auth_method = plaintext

# optional: sasl_scram authentication only
sasl_username = foo
sasl_password = bar

# topic names
topic_products = $KAFKA_TOPIC_PRODUCTS
topic_purchases = $KAFKA_TOPIC_PURCHASES
topic_inventories = $KAFKA_TOPIC_INVENTORIES

[SALES]
# minimum sales frequency in seconds (debug with 1, typical min. 120)
min_sale_freq = 2

# maximum sales frequency in seconds (debug with 3, typical max. 300)
max_sale_freq = 5

# number of transactions to generate
number_of_sales = 5000

# chance of items purchased in a single transaction being 1 vs. 2 or 3 on scale of 1 to 20?
transaction_quantity_one_item_freq = 13

# chance of product quantity being 1 vs. 2 or 3 on scale of 1 to 30?
item_quantity_one_freq = 24

# chance of being member on scale of 1 to 10?
member_freq = 3

# percentage discount for smoothie club members as decimal
club_member_discount = .10

# chance of adding a supplement to group 1 smoothies on scale of 1 to 10?
add_supp_freq_group1 = 5

# chance of adding a supplement to group 2 smoothies on scale of 1 to 10?
add_supp_freq_group2 = 2

# cost of adding supplements to smoothie
supplements_cost = 1.99

[INVENTORY]
# minimum inventory level (higher min. == more restocking events)
min_inventory = 10

# restocking amount (lower amount == more restocking events)
restock_amount = 15
```

and change some of the settings as you like. 

When running the simulator, you can then map the `configuration.ini` file into the container when starting it

```bash
docker run -ti -v configuration.ini:/app/configuration -e KAFKA_BOOTSTRAP_SERVERS=localhost:9092 trivadis/sales-simulator:1.0.0
```