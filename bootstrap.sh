#!/bin/bash
FILE=kafka.tgz
if [ ! -f "$FILE" ]; then
    echo "downloading kafka"
    curl https://apache.osuosl.org/kafka/2.7.0/kafka_2.13-2.7.0.tgz -o kafka.tgz
else
    echo "using local kafka"    
fi
tar -xzf kafka.tgz
cd kafka_2.13-2.7.0

bin/zookeeper-server-start.sh config/zookeeper.properties & \
    bin/kafka-server-start.sh config/server.properties & \
    bin/kafka-topics.sh --create --topic vehicle-positions-download --bootstrap-server localhost:9092