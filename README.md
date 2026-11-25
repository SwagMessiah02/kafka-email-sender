# How to build

## Running application:
```
docker compose up --build
```

## Create topic:
```
docker exec -it broker-kafka /opt/kafka/bin/kafka-topics.sh --create --topic email-input --partitions 3 --bootstrap-server kafka-broker:19092 
```

## Send messages:
```
docker exec -it broker-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server kafka-broker:19092 --topic email-input --producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner
```

## Message example
```
{"to":"teste@email.com","name":"teste","link":"www.example.com"}
```