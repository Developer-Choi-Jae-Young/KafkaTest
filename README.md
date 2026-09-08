# 📤 Spring Kafka Producer Application

> **KafkaTest Project - `pub` Branch**  
> Spring Boot 기반으로 Apache Kafka 브로커에 메시지를 발행(Publish/Produce)하는 메세지 생성 전송 애플리케이션 브랜치입니다.

---

## 📌 1. 프로젝트 개요 (Overview)
`pub` 브랜치는 **Spring Kafka**를 활용하여 Producer 애플리케이션을 구현한 프로젝트입니다.  
Kafka 브로커와의 커넥션 설정, 객체 직렬화(Serialization) 메커니즘, 그리고 Spring Kafka의 핵심 추상화 클래스인 `KafkaTemplate`을 활용하여 특정 토픽(`topic`)으로 메시지를 비동기적으로 발행하는 기능을 포함합니다.

---

## 🎯 2. 연구 및 학습 목적 (Research Purpose)
- **Spring Kafka Producer Configuration 학습**: Kafka 브로커 연결 정보(`BOOTSTRAP_SERVERS_CONFIG`) 및 메시지 Key/Value 직렬화 방식(`StringSerializer`) 구현.
- **`KafkaTemplate` 활용 추상화 검증**: Low-level Kafka Producer API 대신 Spring의 `KafkaTemplate`을 통해 메시지 전송 로직의 편리함 및 캡슐화 확인.
- **Producer-Consumer 메시지 발행 테스트**: Spring Boot 통합 테스트 환경(`SpringBootTest`)에서 Kafka 브로커로 메시지를 전송하고 Consumer와의 연동 흐름 검증.

---

## 📂 3. 프로젝트 구조 (Project Structure)
```
KafkaTest (pub branch)
├── build.gradle
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/demo/
    │   │   ├── KafKaTestApplication.java       # Spring Boot Main Class
    │   │   ├── TestProducer.java               # Kafka 메시지 발행 컴포넌트
    │   │   └── config/
    │   │       └── KafkaProducerConfig.java    # Producer & KafkaTemplate 설정
    │   └── resources/
    │       └── application.properties          # 애플리케이션 프로퍼티
    └── test/java/com/example/demo/
        └── KafKaTestApplicationTests.java     # Producer 메시지 발송 통합 테스트
```

---

## ⚙️ 4. 주요 설정 및 코드 분석 (Key Configurations & Code)

### 1) Kafka Producer Configuration (`KafkaProducerConfig.java`)
```java
@Bean
public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(config);
}

@Bean
public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
}
```

- **`BOOTSTRAP_SERVERS_CONFIG`**
  - Producer가 처음으로 연결할 Kafka 브로커의 네트워크 위치를 설정합니다.  
  - 현재 설정에서는 `localhost:9092` 포트에 위치한 Kafka 브로커로 연결하도록 지정되어 있습니다.
- **`KEY_SERIALIZER_CLASS_CONFIG` & `VALUE_SERIALIZER_CLASS_CONFIG`**
  - Producer가 Key와 Value 데이터 레코드를 Kafka 브로커로 전송하기 전에 바이트 배열(Byte Array)로 변환하는 직렬화 메커니즘을 설정합니다.  
  - Kafka는 네트워크를 통해 데이터를 전송하므로 직렬화 과정이 필요하며, 본 프로젝트에서는 `StringSerializer`를 사용하여 문자열 형태의 데이터를 직렬화합니다.
- **`KafkaTemplate`**
  - Spring Kafka에서 제공하는 Kafka Producer Wrapping 클래스입니다.  
  - 직접 Kafka Producer API를 제어하는 대신 `send()` 와 같은 직관적인 메서드를 통해 편리하고 간결하게 브로커로 메시지를 보낼 수 있습니다.

### 2) Producer Message Component (`TestProducer.java`)
```java
@Component
public class TestProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void create() {
        kafkaTemplate.send("topic", "say hello~");
    }
}
```
- `kafkaTemplate.send("topic", "say hello~")`: `"topic"` 이라는 이름의 Kafka 토픽으로 `"say hello~"` 메시지를 발행합니다.

---

## 🚀 5. 실행 및 테스트 방법 (Usage & Testing)

### 사전 준비 사항
`docker` 브랜치의 Docker Compose를 통해 Zookeeper 및 Kafka 브로커가 사전 구동되어 있어야 합니다 (`localhost:9092`).

### 메시지 발행 테스트 진행
1. Consumer 애플리케이션(`consum` 브랜치)을 먼저 실행하여 토픽 리스너를 가동합니다.
2. `pub` 브랜치 프로젝트의 통합 테스트 메서드를 실행합니다:
   - File: `src/test/java/com/example/demo/KafKaTestApplicationTests.java`
   - Test Method: `test()` (`testProducer.create()` 호출)
3. Kafka 브로커로 `"say hello~"` 메시지가 발송되며, Consumer 콘솔 창에서 데이터 수신을 확인합니다.

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
