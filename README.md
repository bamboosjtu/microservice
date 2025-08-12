# 图书推荐系统

[GITHUB 地址](https://github.com/lombocska/udemy-microservices-example/tree/master)



- [ ] account（/api/v1/account/）:
  - [x] create account api（POST /create-mock-accounts、GET /{id}），and save to posgres
  - [x] send AccountCreatedEvent
  - [x] create searchpreference and save to postgres（POST /preferences）
  - [x] send SearchPreferenceCreatedEvent
  - [ ] listen on SearchPreferenceTriggeredEvent
  - [ ] get accout for SearchPreference
  - [ ] send EmailNotificationTriggeredEvent
- [ ] book（/api/v1/book/）
  - [x] save book to postgres
  - [x] send BookCreatedEvent
- [ ] percolator
  - [x] listen on SearchPreferenceCreatedEvent
  - [x] listen on BookCreatedEvent
  - [ ] save SearchPreference to ES
  - [ ] percolate new Book on saved SearchPreference queries in ES
  - [ ] send SearchPreferenceTriggeredEvent
- [ ] notification
  - [ ] listen on EmailNotificationTriggeredEvent
  - [ ] send email with GOOGLE SMTP

![sequence diagram](C:\Users\theTruth\Documents\projects\book-recommendation-system\sequence diagram.webp)



## 消息队列

Kafaka的默认端口是9092。



| 事件名                            | 建议 Topic 名称                   |
| --------------------------------- | --------------------------------- |
| `AccountCreatedEvent`             | `account-created-topic`           |
| `SearchPreferenceCreatedEvent`    | `search-preference-created-topic` |
| `BookCreatedEvent`                | `book-created-topic`              |
| `SearchPreferenceTriggeredEvent`  | `search.preference.triggered`     |
| `EmailNotificationTriggeredEvent` | `notification.email.triggered`    |



```bash
 chcp 65001
 cd C:\tools\kafka_2.13-3.9.1\
 
 # 1.启动Kafka
 .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
 
 .\bin\windows\kafka-server-start.bat .\config\server.properties
 
 # 2.测试主题功能
 # （1）创建主题
 .\bin\windows\kafka-topics.bat --create --topic account-created-topic \
--bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# （2）查看主题列表
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

# （3）删除主题
.\bin\windows\kafka-topics.bat --delete --topic accountcreated-topic --bootstrap-server localhost:9092

# 3.测试消息功能
# （1）启动生产者
.\bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9092 --topic account-created-topic

# （2）启动消费者
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic account-created-topic --from-beginning
```



## 搜索引擎

开源解决方案：Elasticsearch（简称ES），搭配中文分词器（如[analysis-ik](https://github.com/infinilabs/analysis-ik)分词器和analysis-icu），Kibana控制面板和head插件（Kibana Dev Tools平替）。

ES的默认端口是9200，Kibana的默认端口是5601。

```bash
cd C:\tools\elasticsearch-9.1.0
.\bin\elasticsearch.bat

cd C:\tools\kibana-9.0.0-rc1\
.\bin\kibana.bat

```

### 

### ES基本概念

v8后有较大升级。

| 关系型数据库 | Database | Table | Scheme  | Row      | Column |
| ------------ | -------- | ----- | ------- | -------- | ------ |
| ES7.X        | Index    | Type  | Mapping | Document | Field  |
| ES8.X        | Index    | \     | Mapping | Document | \      |

### 分词器

Elasticsearch 自带的分词器（`standard`）对中文支持很差——会把一个中文句子当成一个整体存储或一个字一个字切开。

IK 分词器是一个 **开源中文分词插件**，能更好地识别中文词语，比如：

```
复制编辑原文：我爱北京天安门
默认分词：我 / 爱 / 北 / 京 / 天 / 安 / 门
IK 分词：我爱 / 北京 / 天安门
```

因此，如果数据里有中文（搜索、匹配、过滤等），IK 分词几乎是必装。



## 容器部署

```bash
docker run -d --name kafka -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
    --network some-network \
    confluentinc/cp-kafka:latest
    
docker run --name elasticsearch \
    -e "discovery.type=single-node" \
    -e "xpack.security.enabled=false" \
    -p 9200:9200 -p 9300:9300 \
    docker.elastic.co/elasticsearch/elasticsearch:8.13.4
    

```

