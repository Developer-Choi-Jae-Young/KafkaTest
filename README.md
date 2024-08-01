ConsumerConfig.GROUP_ID_CONFIG는 Consumer가 속한 Consumer 그룹의 ID를 설정합니다. 
Consumer 그룹은 같은 토픽을 소비하는 Consumer들의 그룹으로, 그룹 내의 모든 Consumer는 토픽의 서로 다른 파티션에서 메시지를 읽어 들입니다.

이를 통해 메시지 처리를 병렬화 하여 처리 속도를 향상시킬 수 있으며, Consumer가 실패할 경우 다른 Consumer가 해당 Consumer의 파티션을 처리하여 고가용성을 제공할 수 있습니다. 
ConcurrentKafkaListenerContainerFactory는 Spring의 @KafkaListener 어노테이션이 붙은 메서드에 주입되어 사용되며, 메시지를 동시에 처리할 수 있는 메시지 리스너 컨테이너를 생성합니다.
