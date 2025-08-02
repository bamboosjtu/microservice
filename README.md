# 图书推荐系统



- [ ] account（/api/v1/account/）:
  - [x] create account api（POST /create-mock-accounts、GET /{id}），and save to posgres
  - [x] AccountCreatedEvent
  - [ ] create searchpreference and save to postgres（POST /preferences）
  - [ ] SearchPreferenceCreatedEvent

![sequence diagram](C:\Users\theTruth\Documents\projects\book-recommendation-system\sequence diagram.webp)



## 消息主题



| 事件名                            | 建议 Topic 名称                |
| --------------------------------- | ------------------------------ |
| `AccountCreatedEvent`             | `account.created`              |
| `SearchPreferenceCreatedEvent`    | `search.preference.created`    |
| `BookCreatedEvent`                | `book.created`                 |
| `SearchPreferenceMatchedEvent`    | `search.preference.matched`    |
| `EmailNotificationTriggeredEvent` | `notification.email.triggered` |



```bash
 cd C:\tools\kafka_2.13-3.9.1\
 
 # 启动Kafka
 .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
 .\bin\windows\kafka-server-start.bat .\config\server.properties
 
 # 测试Kafka功能
 # 1.创建主题
 .\bin\windows\kafka-topics.bat --create --topic accountcreated-topic \
--bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# 2.查看主题列表
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

# 3.启动生产者
.\bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9092 --topic accountcreated-topic

# 4.启动消费者
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic accountcreated-topic --from-beginning
```



# 容器部署

```bash
docker run -d --name kafka -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
    --network some-network \
    confluentinc/cp-kafka:latest

```

