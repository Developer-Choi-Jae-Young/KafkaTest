# 🐳 Kafka & Zookeeper Docker Infrastructure

> **KafkaTest Project - `docker` Branch**  
> Apache Kafka 클러스터 및 Zookeeper 인프라 환경 구축을 위한 Docker Compose 설정 브랜치입니다.

---

## 📌 1. 프로젝트 개요 (Overview)
`docker` 브랜치는 로컬 개발 환경에서 별도의 복잡한 설치 과정 없이 **Apache Kafka** 브로커와 **Zookeeper**를 컨테이너화하여 빠르게 구동할 수 있도록 지원하는 인프라 브랜치입니다.  
`pub` (Producer) 브랜치와 `consum` (Consumer) 브랜치의 애플리케이션이 상호 메시지를 주고받을 수 있는 중앙 메시지 브로커 역할을 수행합니다.

---

## 🎯 2. 연구 및 학습 목적 (Research Purpose)
- **컨테이너 기반 Kafka 클러스터 구축**: Docker Compose를 활용해 Zookeeper와 Kafka 브로커 간의 의존성을 정의하고 컨테이너 환경에서 메시지 브로커를 가동하는 방법 이해.
- **Kafka 네트워크 및 포트 바인딩 설정**: 브로커의 리스너(`LISTENERS`, `ADVERTISED_LISTENERS`) 설정과 호스트-컨테이너 간 포트 포워딩 메커니즘 학습.
- **개발/테스트 인프라 격리**: 로컬 환경과의 독립성을 유지하면서 Spring Boot 애플리케이션(`pub`, `consum`)과의 안정적인 연동 환경 구성.

---

## 📂 3. 디렉토리 구조 (Directory Structure)
```
KafkaTest (docker branch)
├── .gitignore
├── README.md                  # Docker 인프라 안내 문서
└── Kafka/
    └── docker-compose.yml     # Zookeeper & Kafka 컨테이너 구성 파일
```

---

## ⚙️ 4. 주요 설정 내용 (Configuration Details)

### `Kafka/docker-compose.yml`
```yaml
version: "3"
services:
  zookeeper:
    image: "bitnami/zookeeper:latest"
    ports:
      - "2181:2181"
    environment:
      - ALLOW_ANONYMOUS_LOGIN=yes

  kafka:
    image: "bitnami/kafka:latest"
    ports:
      - "9092:9092"
    environment:
      - KAFKA_BROKER_ID=1
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092
      - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
      - ALLOW_PLAINTEXT_LISTENER=yes

    depends_on:
      - zookeeper
```

### 핵심 환경 변수 설명
- **Zookeeper (`bitnami/zookeeper`)**
  - `ALLOW_ANONYMOUS_LOGIN=yes`: 개발 및 테스트용으로 익명 로그인 허용.
  - `2181:2181`: Zookeeper 기본 클라이언트 포트 바인딩.
- **Kafka (`bitnami/kafka`)**
  - `KAFKA_BROKER_ID=1`: Kafka 클러스터 내에서 해당 브로커를 식별하는 고유 ID.
  - `KAFKA_CFG_LISTENERS=PLAINTEXT://:9092`: Kafka 브로커가 내부적으로 수신 대기할 네트워크 리스너 설정.
  - `KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://127.0.0.1:9092`: 클라이언트(Producer, Consumer)가 브로커에 접속할 때 사용할 주소 및 포트.
  - `KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181`: Kafka가 상태 관리 및 메타데이터 저장을 위해 연동할 Zookeeper 주소.
  - `depends_on`: Zookeeper 컨테이너가 우선 실행된 후 Kafka 브로커가 실행되도록 의존성 설정.

---

## 🚀 5. 실행 방법 (Usage)

### 1) 컨테이너 구동
`Kafka` 디렉토리로 이동하여 Docker Compose를 통해 실행합니다.
```bash
# Docker Compose 백그라운드 실행
docker-compose -f Kafka/docker-compose.yml up -d

# 실행 상태 확인
docker ps
```

### 2) 컨테이너 종료 및 정리
```bash
# Docker Compose 컨테이너 정지 및 삭제
docker-compose -f Kafka/docker-compose.yml down
```

---

## 🔗 6. 전체 연계 구조 (System Architecture)

```
+-------------------------------------------------------------------+
|                        Docker Container                           |
|                                                                   |
|   +-------------------+              +------------------------+   |
|   | Zookeeper         | <----------- | Kafka Broker           |   |
|   | (Port: 2181)      |              | (Port: 9092)           |   |
|   +-------------------+              +------------------------+   |
+---------------------------------------------------^---------------+
                                                    |
                      +-----------------------------+-----------------------------+
                      |                                                           |
          +-----------+------------+                                  +-----------+------------+
          | Producer Application   |                                  | Consumer Application   |
          | (pub branch)           |                                  | (consum branch)        |
          +------------------------+                                  +------------------------+
```

1. **`docker` 브랜치**: Zookeeper 및 Kafka 브로커 인프라를 실행 (`localhost:9092`).
2. **`pub` 브랜치**: Spring Boot Producer에서 `localhost:9092` 로 메시지 발행 (`KafkaTemplate.send`).
3. **`consum` 브랜치**: Spring Boot Consumer에서 `localhost:9092` 로 메시지 수신 (`@KafkaListener`).
