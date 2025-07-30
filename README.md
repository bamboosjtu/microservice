# 图书推荐系统



![sequence diagram](C:\Users\theTruth\Documents\projects\microservice\sequence diagram.webp)



```bash
 cd C:\tools\kafka_2.13-3.9.1\
 
 # 启动Kafka
 .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
 .\bin\windows\kafka-server-start.bat .\config\server.properties
 
 # 测试Kafka功能
 # 1.创建主题
 .\bin\windows\kafka-topics.bat --create --topic test-topic \
--bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# 2.查看主题列表
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

# 3.启动生产者
.\bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9092 --topic test-topic

# 4.启动消费者
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic test-topic --from-beginning
```

