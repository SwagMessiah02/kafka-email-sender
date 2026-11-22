# How to build

## Create topic:
```
docker exec -it broker-kafka /opt/kafka/bin/kafka-topics.sh --create --topic email-input --partitions 3 --bootstrap-server kafka-broker:19092 
```

## Send messages:
```
docker exec -it broker-kafka /opt/kafka/bin/kafka-console-producer.sh --topic email-input --bootstrap-server kafka-broker:19092 
```
