# 📥 Spring Kafka Consumer Application

> **KafkaTest Project - `consum` Branch**  
> Spring Boot 기반으로 Apache Kafka 브로커로부터 메시지를 수신(Consume/Subscribe)하고 처리하는 메시지 소비 애플리케이션 브랜치입니다.

---

## 📌 1. 프로젝트 개요 (Overview)
`consum` 브랜치는 **Spring Kafka**를 활용하여 Consumer 애플리케이션을 구현한 프로젝트입니다.  
Kafka 브로커로부터 메시지를 끌어와(Pull) 역직렬화(Deserialization)하고, Consumer Group 관리 및 `@KafkaListener` 어노테이션을 통해 메시지를 병렬적으로 수신 및 처리하는 메커니즘을 다룹니다.

---

## 🎯 2. 연구 및 학습 목적 (Research Purpose)
- **Consumer Group & High Availability 이해**: Consumer Group ID 설정(`group_1`) 및 메시지 처리 병렬화, 장애 대응(Failover) 구조 학습.
- **Spring Kafka Listener Container 구축**: `ConcurrentKafkaListenerContainerFactory` 및 `DefaultKafkaConsumerFactory`를 통해 멀티 스레드 기반 메시지 리스너 컨테이너 구성.
- **`@KafkaListener`를 이용한 이벤트 기반 수신**: 토픽(`topic`)을 실시간으로 감지하고 수신된 메시지를 처리하는 이벤트 리스닝 메커니즘 검증.

---

## 📂 3. 프로젝트 구조 (Project Structure)
```
KafkaTest (consum branch)
├── build.gradle
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/demo/
    │   │   ├── KafKaTestApplication.java       # Spring Boot Main Application
    │   │   ├── TestConsumer.java               # Kafka @KafkaListener 메시지 수신 컴포넌트
    │   │   └── config/
    │   │       └── KafkaConsumerConfig.java    # Consumer & ListenerContainerFactory 설정
    │   └── resources/
    │       └── application.properties          # 애플리케이션 프로퍼티
    └── test/java/com/example/demo/
        └── KafKaTestApplicationTests.java     # 통합 테스트
```

---

## ⚙️ 4. 주요 설정 및 코드 분석 (Key Configurations & Code)

### 1) Kafka Consumer Configuration (`KafkaConsumerConfig.java`)
```java
@Bean
public ConsumerFactory<String, Object> consumerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    return new DefaultKafkaConsumerFactory<>(config);
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());

    return factory;
}
```

- **`ConsumerConfig.GROUP_ID_CONFIG ("group_1")`**
  - Consumer가 속한 **Consumer Group**의 ID를 설정합니다.  
  - Consumer Group은 동일한 토픽을 소비하는 Consumer들의 논리적 그룹으로, 그룹 내의 모든 Consumer는 토픽의 서로 다른 파티션(Partition)에서 메시지를 읽어 들입니다.  
  - 이를 통해 메시지 처리를 병렬화하여 처리 속도를 향상시킬 수 있으며, 특정 Consumer가 실패(Failure)할 경우 그룹 내 다른 Consumer가 해당 파티션을 분담 처리하여 고가용성(High Availability)을 제공합니다.
- **`KEY_DESERIALIZER_CLASS_CONFIG` & `VALUE_DESERIALIZER_CLASS_CONFIG`**
  - Kafka 브로커로부터 수신한 바이트 배열(Byte Array) 형태의 데이터를 자바 객체(문자열)로 변환하는 역직렬화 클래스(`StringDeserializer`)를 지정합니다.
- **`ConcurrentKafkaListenerContainerFactory`**
  - Spring의 `@KafkaListener` 어노테이션이 붙은 메서드에 주입되어 사용되며, 메시지를 동시에 처리할 수 있는 메시지 리스너 컨테이너(Message Listener Container)를 생성하는 팩토리 클래스입니다.

### 2) Consumer Listener Component (`TestConsumer.java`)
```java
@Component
public class TestConsumer {
    @KafkaListener(topics = "topic", groupId = "group_1")
    public void listener(Object data) {
        System.out.println(data);
    }
}
```
- `@KafkaListener`: `"topic"` 토픽에 데이터가 Push되는 순간 이벤트를 감지하여 메서드를 실행하고, 전달받은 데이터를 콘솔에 출력합니다.

---

## 🚀 5. 실행 및 테스트 방법 (Usage & Testing)

### 사전 준비 사항
`docker` 브랜치의 Docker Compose를 통해 Zookeeper 및 Kafka 브로커가 사전 구동되어 있어야 합니다 (`localhost:9092`).

### 메시지 수신 및 연동 테스트
1. Consumer 애플리케이션(`consum` 브랜치)의 `KafKaTestApplication.java`를 실행하여 서버를 가동합니다.
2. Producer 애플리케이션(`pub` 브랜치)에서 메시지 전송 테스트(`KafKaTestApplicationTests.java`)를 실행합니다.
3. Consumer 애플리케이션의 콘솔 로그에 Producer가 보낸 `"say hello~"` 메시지가 정상 출력되는지 확인합니다.

---

## 🔗 6. 전체 연계 구조 (System Architecture)

```
[ pub 브랜치 ]                      [ docker 브랜치 ]                   [ consum 브랜치 ]
+-------------------+               +-------------------+               +-------------------+
| Spring Boot       |  send()       | Kafka Broker      |  @KafkaListen | Spring Boot       |
| Producer App      | ------------> | (Topic: "topic")  | ------------> | Consumer App      |
| (TestProducer)    |               | (Port: 9092)      |               | (TestConsumer)    |
+-------------------+               +-------------------+               +-------------------+
```
