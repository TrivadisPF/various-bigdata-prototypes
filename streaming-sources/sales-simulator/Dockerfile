# Use an official Python runtime as a base image
FROM python:3.10-slim

# Set the working directory in the container
WORKDIR /app

# Copy the requirements.txt file to the working directory
COPY requirements.txt .

# Install the dependencies from requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Copy the current directory contents into the container at /app
COPY . .

ENV KAFKA_BOOTSTRAP_SERVERS=localhost:9092
ENV KAFKA_SCHEMA_REGISTRY_URL=http://localhost:8081
ENV KAFKA_MESSAGE_FORMAT=json
ENV KAFKA_TOPIC_PRODUCTS=demo.products
ENV KAFKA_TOPIC_PURCHASES=demo.purchases
ENV KAFKA_TOPIC_INVENTORIES=demo.inventories

# Command to run your application
CMD ["python", "producer.py"]
