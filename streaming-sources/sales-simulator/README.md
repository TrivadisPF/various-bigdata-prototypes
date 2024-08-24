# Sales Simulator

This project's highly configurable, Python-based, synthetic data generator (producer.py) streams product listings, sales transactions, and inventory restocking activities to Apache Kafka topics.

It is based on the excellent work of Gary Stafford available here: <https://github.com/garystafford/streaming-sales-generator>. 

I have basically wrapped it as a Docker container so that it can be easily started or added to an existing docker-based environment. 