#!/bin/bash

echo "Waiting for Kafka to be ready..."
sleep 30

echo "Creating Kafka topics for task service..."

# Создаем топик для событий задач
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists \
  --topic task.events --partitions 3 --replication-factor 1

# Создаем топик для уведомлений
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists \
  --topic notification.events --partitions 3 --replication-factor 1

echo "Kafka topics created successfully!"
