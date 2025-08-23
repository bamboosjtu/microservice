# 图书推荐系统

[GITHUB 地址](https://github.com/lombocska/udemy-microservices-example/tree/master)

## 项目总结

1. 学习技术设计，按照时序图开发程序。
2. 学习了消息队列和搜索引擎。
3. 事件机制的设计模式。
4. 相比原版教程，提升了一下版本，尤其是ES在v7以后，用法有比较大的变化。
5. 在chatgpt的指导下编程。



## 接口清单

- [x] account（/api/v1/account/）:
  - [x] create account api（POST /create-mock-accounts、GET /{id}），and save to posgres
  - [x] send AccountCreatedEvent
  - [x] create searchpreference and save to postgres（POST /preferences）
  - [x] send SearchPreferenceCreatedEvent
  - [x] listen on SearchPreferenceTriggeredEvent
  - [x] get accout for SearchPreference
  - [x] send EmailNotificationTriggeredEvent
- [x] book（/api/v1/book/）
  - [x] save book to postgres
  - [x] send BookCreatedEvent
- [x] percolator
  - [x] listen on SearchPreferenceCreatedEvent
  - [x] save SearchPreference Query to ES
  - [x] listen on BookCreatedEvent
  - [x] percolate new Book on saved SearchPreference queries in ES
  - [x] send SearchPreferenceTriggeredEvent
- [x] notification
  - [x] listen on EmailNotificationTriggeredEvent
  - [x] send email with GOOGLE SMTP

![sequence diagram](C:\Users\theTruth\Documents\projects\book-recommendation-system\sequence diagram.webp)



## 消息队列

Kafaka的默认端口是9092。



| 事件名                            | 建议 Topic 名称                      |
| --------------------------------- | ------------------------------------ |
| `AccountCreatedEvent`             | `account-created-topic`              |
| `SearchPreferenceCreatedEvent`    | `search-preference-created-topic`    |
| `BookCreatedEvent`                | `book-created-topic`                 |
| `SearchPreferenceTriggeredEvent`  | `search-preference-triggered-topic`  |
| `EmailNotificationTriggeredEvent` | `notification-email-triggered-topic` |



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

# （3）删除主题，似乎总是遇到问题，直接删除/tmp/kafka-logs和/tmp/zookeeper
.\bin\windows\kafka-topics.bat --delete --topic account-created-topic --bootstrap-server localhost:9092

# 3.测试消息功能
# （1）启动生产者
.\bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9092 --topic account-created-topic

# （2）启动消费者
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic account-created-topic --from-beginning
```



### 事件驱动微服务架构：契约接口

```java
package me.bamboo.common.base;

import java.time.Instant;
import java.util.UUID;
/*
接口设计概览
EventContract<T> 接口是一个泛型接口，它定义了一个事件（Event）所必须包含的标准元数据（metadata）。接口的核心思想是为所有事件提供一个统一的“信封”（Event Envelope），这种设计模式在事件驱动架构（Event-Driven Architecture, EDA）中非常常见，无论事件的业务内容是什么，它都必须包含一套标准的元数据，这对于事件的追踪、审计和处理至关重要。

该接口的主要设计目标是：
- 标准化事件数据：无论事件的具体业务内容是什么，每个事件都必须符合这个统一的契约，包含一套标准的字段，如 ID、时间戳、来源等。这对于跨多个微服务进行事件处理和监控至关重要。
- 支持泛型：通过使用 <T> 泛型，该接口能够适应任何类型的业务数据负载（Payload），使得一个通用的接口可以被多个不同的业务事件所实现。例如，UserCreatedEvent 可以实现 EventContract<UserCreatedPayload>，而 OrderShippedEvent 可以实现 EventContract<OrderShippedPayload>。
*/
public interface EventContractor<T> {
	String version = "1.0";
	
	UUID getId();//事件的唯一标识符。这对于事件的幂等性处理和追踪非常重要。
	String getSource();//表示生成此事件的微服务或系统。这对于调试和理解事件流至关重要。
	String getType();//事件的类型，通常是一个动词的过去式，表示一个已经发生的动作，例如 "user.created" 或 "order.shipped"。这使消费者能够根据事件类型决定如何处理它。
	String getVersion();//事件契约的版本号。这很重要，因为它允许对事件的信封结构进行演进，而不会破坏旧版本的兼容性。
	Instant getCreated();//事件创建时的 UTC 时间戳。这是事件时序和审计的关键信息。
	UUID getCorrelationId();//关联 ID。这是该设计中的一个关键且强大的概念。它用于将一系列逻辑上相关的事件串联起来。例如，一个用户的创建事件可能触发下游服务生成一个欢迎邮件事件，这两个事件会共享同一个 correlationId。这对于分布式系统中的端到端事务追踪和可观察性（Observability）至关重要。
	T getPayload();//事件的实际业务内容。这是泛型 <T> 的用武之地。例如，一个订单事件的 Payload 可能包含订单号、商品列表和总价等信息。
	String getAggregateId();//这是一个可选字段，但非常重要。它通常指向一个业务实体（如用户ID或订单号）。如果多个事件与同一个业务实体相关，并且需要按顺序处理（例如，order.created 和 order.updated），则可以使用 aggregateId 来确保消息按顺序处理，或者至少让消费者知道何时需要处理顺序问题。	
}
```



采用了一种分层的、事件驱动的设计模式，主要分为两个层次，[在线展示UML](http://www.plantuml.com/plantuml/uml)。

1. **通用层：定义了所有事件的**通用契约和基础实现**，确保了事件模型的标准化。
2. **领域层 **：专注于业务事件**，它继承和扩展了通用层的功能，将业务数据和类型安全封装在事件中。

```
@startuml

' -------------------- Base Package --------------------
package "common.base" {
    interface EventContract<T> {
        +getId()
        +getSource()
        +getType()
        +getVersion()
        +getCreated()
        +getCorrelationId()
        +getPayload()
        +getAggregateId()
    }

    class DomainEvent<T> {
        -id
        -source
        -type
        -version
        -created
        -correlationId
        -aggregateId
        -payload
    }

    DomainEvent ..|> EventContract
}


' -------------------- Account Package --------------------
package "common.account" {
    abstract class AccountEvent {
        -eventType
        +getEventName()
    }

    class AccountCreatedEvent {
        -id
        -lastName
        -firstName
        -email
    }

    class AccountDomainEvent<T> {
        +SOURCE
    }

    ' Relationships
    AccountCreatedEvent --|> AccountEvent
    AccountDomainEvent  --|> DomainEvent

    ' Composition/Aggregation (AccountDomainEvent has a Payload of type T, which extends AccountEvent)
    AccountDomainEvent o-- AccountEvent : T payload
  
}
@enduml
```



### 序列化与反序列化

`jackson-annotations` 是 **Jackson 库的核心模块之一**，它的作用是提供一组 **Java 注解（Annotations）**，用于控制 **JSON 与 Java 对象之间的序列化（Serialization）和反序列化（Deserialization）行为**。它是 Jackson 三大核心模块之一：

| 模块                  | 作用                                         |
| --------------------- | -------------------------------------------- |
| `jackson-core`        | 核心流式 API（`JsonParser`,`JsonGenerator`） |
| `jackson-databind`    | 对象绑定（`ObjectMapper`）                   |
| `jackson-annotations` | 注解支持（控制序列化/反序列化行为）          |

`jackson-annotations`它不包含运行时逻辑，只提供 **注解定义**，这些注解被 `jackson-databind`（如 `ObjectMapper`）在运行时读取，从而影响 JSON 处理行为。

#### 常用注解

| 注解                  | 作用                                                  |
| --------------------- | ----------------------------------------------------- |
| @JsonProperty("name") | 字段命名控制，指定 JSON 中的字段名                    |
| @JsonIgnore           | 忽略该字段                                            |
| @JsonIgnoreProperties | 全局忽略某些属性（类级别）                            |
| @JsonInclude          | 全局控制 null/empty 字段是否序列化                    |
| @JsonTypeInfo         | 全局策略，启用类型信息                                |
| @JsonTypeName         | 给一个类指定一个逻辑名称，一般与@JsonTypeInfo搭配使用 |
| @JsonSubTypes         | 定义子类型映射，一般与@JsonTypeInfo搭配使用           |



## 搜索引擎

开源解决方案：Elasticsearch（简称ES），搭配中文分词器（如[analysis-ik](https://github.com/infinilabs/analysis-ik)分词器和analysis-icu），Kibana控制面板和head插件（Kibana Dev Tools平替）。

ES的默认端口是9200，Kibana的默认端口是5601。

```bash
cd C:\tools\elasticsearch-9.1.0
.\bin\elasticsearch.bat

cd C:\tools\kibana-9.0.0-rc1\
.\bin\kibana.bat
```

###  ES基本概念

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

### 反向查询（percolator）

在 Elasticsearch 中，**percolator 索引初始化**的核心步骤包括：创建索引设置并定义映射  → 注册查询 → 匹配文档。下面是详细流程，基于 **Elasticsearch 9.x + Spring Boot** 环境。[官方文档](https://www.elastic.co/docs/reference/query-languages/query-dsl/query-dsl-percolate-query)。

#### STEP1：创建索引并定义mapping

在映射（mapping）中，你需要为字段指定 `"type": "percolator"`，用于存储查询 DSL。其他字段存放用户的元信息或条件，虽然**QWEN**说不需要，但实测必须在mapping中加上，不然会报错。

默认情况下，Elasticsearch **不会自动创建正确的 Percolator 映射**，即使你使用 `@Document` 注解。

```http
PUT /percolator_index
{
  "mappings": {
    "properties": {
      "query": {
        "type": "percolator"
      },
      "price": {
        "type": "double"
      },
      "booktype":{
        "type": "keyword"
      }
    }
  }
}
```

#### STEP2：注册查询

每个用户的偏好条件就是一条 **percolator 查询文档**，比如：

```http
POST /search-preferences/_doc
{
  "query": {
    "bool": {
      "filter": [
        { "terms": { "booktype": ["ROMANTIC"] } },
        { "range": { "price": { "gte": 50 } } }
      ]
    }
  }
}
```

#### STEP3：匹配文档

传入一条新文档，看看哪些规则匹配它：

```http
POST /search-preferences/_search
{
  "query": {
    "percolate": {
      "field": "query",
      "document": {
        "booktype": "ROMANTIC",
        "price": 100.0
      }
    }
  }
}
```

#### 使用场景

- **实时告警系统**：用户设置告警条件，当新日志或事件满足条件时触发通知。
- **个性化推荐/订阅通知**：用户订阅关键词或条件，新内容发布时推送。
- **规则引擎**：将业务规则以查询形式注册，新数据进入时自动触发规则。
- **安全监控**：检测日志中是否出现可疑行为模式。

#### 替代方案

根据 Elastic 官方文档和社区实践，单个索引中注册的 percolator 查询数量应控制在 1,000 到 10,000 之间，超过后性能显著下降。部分替代方案如下：

- **Elasticsearch Watcher + Transform**：用于告警。
- **实时流处理（如 Kafka + Flink）**：做复杂事件处理（CEP）。
- **专用规则引擎**：如 Drools。
- **滚动索引 + 普通搜索**：反向思维：定期搜索“未通知的文档是否满足某些条件”。



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

