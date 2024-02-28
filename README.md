# [Anchor] 멘토링 매칭 플랫폼

- [💡 서비스 배경 및 목표](#💡-서비스-배경-및-목표)
- [🛠️ 기술 스택](#🛠️-기술-스택)
- [🗺️ 서버 구조](#🗺️-서버-구조)
- [🗂️ 패키지 구조](#🗂️-패키지-구조)
- [🔥 기술적 개선 및 고려](#🔥-기술적-개선-및-고려)

## 💡 서비스 배경 및 목표

IT 취업 시장의 기준이 점점 높아지고 있으며, 이러한 흐름 속에서 실무적인 역량이 더욱 중요해지고 있습니다.</br>
신뢰할 수 있는 현업자의 직간접적 경험 공유에 대한 필요성과 수요를 확인했습니다.

Anchor 서비스의 목표는 다음과 같습니다.<br>

- 목표 1. 재직중 혹은 재직했던 회사의 이메일 인증을 통해 신뢰성 있는 멘토를 만날 수 있도록 합니다.</br>
- 목표 2. 실시간 알림, 정산과 같은 기능을 통해 서비스 이용에 대한 사용자 편의성을 확보합니다.</br>
- 목표 3. 원한할 서비스 운영을 위한 기술적 요소를 적용하고 개선합니다. (ex. DB 이중화)

## 🛠️ 기술 스택

| 분류       | 기술명                                                                        |
|----------|----------------------------------------------------------------------------|
| BackEnd  | Java, Spring (Boot, Security, JPA), QueryDsl, Junit, Mockito, Redis, MySql |
| FrontEnd | HTML, Javascript, Thymeleaf                                                |
| DevOps   | nGrinder, Jmeter, EC2, RDS, S3, CodeDeploy, GithubAction                   |
| Tools    | IntelliJ, Gradle, Maven                                                    |

## 🗺️ 서버 구조

![Anchor Server Architecture](readme/image/architecture/architecture.png)

## 💾 DB 구조

![Anchor DB Architecture](readme/image/erd/erd.png)

## 🗂️ 패키지 구조

```
- com.anchor
    - domain
        - api
            - controller
            - request
        - domain
            - (Entity, Type Object)
            - response
            - repository
    - global
        - config
        - exception
        - util
        - (...)
```

## 🔥 기술적 개선 및 고려

```
다음과 같은 구조로 작성돼 있으니 참고부탁드립니다. 🙇

- 상황 및 목표 [link]
    - 목표 달성을 위한 행동
        - 결과 및 추가사항
```

### 홈페이지 조회 성능 개선 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/domain/mentoring/api/service/MentoringService.java#L281-L292) / [설정 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/config/CacheConfig.java#L19C1-L39C4)]

- `Caffeine Cache`를 도입해 10개의 인기 멘토링과 태그 조회 시 캐싱 처리

  <details>
  <summary>홈페이지 조회에 대한 부하테스트 결과, 캐싱 미적용 대비 약 80%의 TPS 성능 향상</summary>
  <div>
      <h4>[Ngrinder]</h4>
      <span>Cache 미적용</span>
      <img src="readme/image/cache/ngrinder_nocache.png">
      <span>Cache 적용</span>
      <img src="readme/image/cache/ngrinder_cache.png">
  </div>
  <div>
      <h4>[Jmeter]</h4>
      <span>Cache 미적용</span>
      <img src="readme/image/cache/jmeter_nocache_1.png">
      <img src="readme/image/cache/jmeter_nocache_2.png">
      <span>Cache 적용</span>
      <img src="readme/image/cache/jmeter_cache_1.png">
      <img src="readme/image/cache/jmeter_cache_2.png">
  </div>
  </details>

### 멘토 이메일 발송 응답속도 개선 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/mail/AsyncMailSender.java#L25C1-L37C4) / [설정 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/config/AsyncConfig.java#L16C1-L31C4)]

- 멀티 스레딩을 이용한 `@Async 비동기` 처리
  <details>
  <summary>테스트 결과, 클라이언트 기준 응답시간을 약 3s에서 100ms로 단축</summary>
      <img src="readme/image/async/sync_rt.png">
      <img src="readme/image/async/async_rt.png">
  </details>
- 비동기 작업 스레드의 로깅을 위해 `@Around`를 적용
  <details>
  <summary>테스트 결과, 비동기 작업에 대한 로그 확인</summary>
      <img src="readme/image/async/mail_log_1.png">
      <img src="readme/image/async/mail_log_2.png">
  </details>

### 멘토링 검색 정확도, 정밀도 개선 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/domain/mentoring/domain/repository/custom/QMentoringRepositoryImpl.java#L190C1-L223C2) / [설정 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/config/CustomFunctionContributor.java#L9C1-L21C2)]

- `Full Text Index(ngram parser)`를 적용해 키워드가 일치하는 정도를 수치화
    - like + wildcard 검색 대비 정확하고 정밀한 검색 결과 반환
- `ngram_token_size` 옵션 값을 3(default)에서 2로 조정
    - 2글자 이상부터 검색 가능하도록 변경

### DB에 대한 부하 분산 [[설정 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/db/DataSourceConfig.java#L28C1-L125C2) / [구성 패키지](https://github.com/Team-RecruTe/Anchor-Service/tree/develop/src/main/java/com/anchor/global/db)]

- 로컬/배포 환경에서 `DB 서버 이중화` 구성

    - 로컬 환경: MySQL DB 이중화 (Binary Log 기반)
    - 배포 환경: Aurora DB 이중화 (Page 기반)

- Master-Slave DB 간의 `Write/Read 쿼리 분산` 적용

    - 옵션1. @Transactional의 readOnly 속성을 이용한 쿼리 분산
    - 옵션2. @RouteDataSource의 dataSourceType 속성을 이용한 쿼리 분산
    - (@Transactional: 스프링 어노테이션 / @RouteDataSource: 커스텀 어노테이션)

- 추가 고려사항. 고가용성 확보를 위해서 Master DB 장애에 대한 대비책 필요

### Fetch Join / Pagination 동시 수행 시 메모리 누수 개선 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/domain/mentoring/domain/repository/custom/QMentoringRepositoryImpl.java#L62C1-L92C1)]

- Fetch Join과 Pagination을 동시에 수행하던 단일 쿼리를 `이중 쿼리`로 분리
    - Fetch Join과 Pagination을 별도로 수행해 메모리 누수 문제 해결

### 실시간 알림 기능 구현 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/domain/notification/api/service/NotificationService.java#L40C1-L109C4) / [구성 패키지](https://github.com/Team-RecruTe/Anchor-Service/tree/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/redis/message)]

- 네트워크 자원을 고려해 `Server Sent Event` 스펙으로 클라이언트에게 실시간 알림 전송
    - 웹 페이지 체류시간을 고려해 SSE Timeout 값을 60s로 지정
- 서버 분산을 고려해 `Redis Pub/Sub`을 이용해 실시간 알림 이벤트 발행 및 수신 처리
    - 이벤트 발행자와 수신자가 연결된 서버가 다르더라도 알림 수신 가능

### 멘토링 신청자 수 동시성 제어 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/redis/lock/RedisLockFacade.java#L20C1-L41C4) / [구성 패키지](https://github.com/Team-RecruTe/Anchor-Service/tree/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/redis/lock)]

- 서버 분산을 고려해 `Facade 패턴`과 `Redis Lock`을 이용해 동시성 제어 구현
    - pub/sub을 이용해 Lock 획득을 시도하는 RedissonClient를 통해 CPU 낭비 방지

### S3 저장소 내 불필요한 이미지 삭제 자동화 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/domain/readme/image/api/service/ImageService.java#L31C1-L40C4)]

- 트래픽이 적은 시간을 고려해 `@Sceduled`를 이용해 이미지 삭제를 요청하는 스케줄링 구현
    - 매일 새벽 3시에 이미지 삭제 요청 스케줄링 동작

### 이미지 파일의 효율적인 관리 [[적용 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/java/com/anchor/global/valid/CustomValidatorRegistry.java#L13C1-L44C2) / [구성 패키지](https://github.com/Team-RecruTe/Anchor-Service/tree/develop/src/main/java/com/anchor/global/valid)]

- `@ValidFile` 커스텀 어노테이션을 통해 빈 이미지 파일 요청에 대한 검증 처리
    - ConstraintValidator 클래스를 구현해 Custom Validator 직접 정의
- [이미지 요청 시 파일당 10MB 용량 제한](https://github.com/Team-RecruTe/Anchor-Service/blob/fe37c7b7a98d0511150b2ba4dd09574adfb07e82/src/main/resources/application.yml#L22C1-L25C29)

### 로그 메세지 최적화 [[설정 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/develop/src/main/resources/log4j2/log4j2.yml)]

- 속도가 빠른 `AsyncLogger` 옵션을 제공하는 Log4j2 라이브러리를 도입해 로깅 처리
    - Appender: console-appender와 rolling-file-appender 적용
    - Logger: rolling-file-appender에 대해서 AsyncLogger 부분 적용

### CI/CD 환경 구축 [[설정 코드](https://github.com/Team-RecruTe/Anchor-Service/blob/develop/.github/workflows/cicd.yml)]

- `Github Actions`, `AWS CodeDeploy`, `S3`를 이용해 테스트-빌드-배포 자동화
    - 빌드 서버는 Github Runner 서버 사용

### 회원 정보 관리 포인트 최소화 [[구성 패키지](https://github.com/Team-RecruTe/Anchor-Service/tree/develop/src/main/java/com/anchor/global/auth)]

- `OAuth 2.0 & OpenID Connect`를 이용해 Naver, Google 인증 기능 구현
    - 인증 회원의 정보는 Redis 분산 세션에 SessionUser 객체를 저장

### Insert 쿼리 성능 개선

- `JPQL Bulk Insert`를 적용
  <details>
  <summary>1만건의 데이터 삽입에 대해, Insert 쿼리 대비 약 60%의 성능 개선</summary>
      <img src="readme/image/insert/image.png">
      <img src="readme/image/insert/image-1.png">
  </details>
