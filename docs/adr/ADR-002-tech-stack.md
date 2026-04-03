# ADR-002: 기술 스택 선택

- **날짜**: 2026-04-03
- **상태**: 채택됨

---

## 배경

부동산 임장 기록 앱의 백엔드를 새로 구축하면서 언어·프레임워크·DB를 선택해야 했다.
주요 고려 기준은 다음과 같다.

- 지도 기반 서비스이므로 공간 쿼리 지원이 필수
- 소규모 팀이 빠르게 MVP를 출시해야 함
- 향후 AWS 클라우드 배포를 전제로 함

---

## 결정

| 영역 | 선택 | 주요 대안 |
|---|---|---|
| 언어 | Kotlin | Java, Go, Node.js |
| 프레임워크 | Spring Boot 3 | Ktor, Express, FastAPI |
| DB | PostgreSQL 15 + PostGIS | MySQL, MongoDB, DynamoDB |
| 캐시 | Redis | Memcached, 로컬 캐시 |
| 클라이언트 | React Native | Flutter, Swift/Kotlin Native |
| 인프라 | Docker + AWS ECS | Kubernetes, Heroku, Railway |

---

## 이유

### Kotlin + Spring Boot 3
- Kotlin은 Java 생태계를 그대로 사용하면서 null 안정성·data class·코루틴 등 생산성이 높음.
- Spring Boot 3는 Jakarta EE 기반으로 장기 지원이 보장되며, JPA·Security·Validation 등 필요한 스타터가 모두 있음.
- Java 21 LTS + Spring Boot 3의 Virtual Thread 지원으로 향후 성능 확장 여지 있음.

### PostgreSQL 15 + PostGIS
- 오픈소스 RDBMS 중 공간 확장(PostGIS)이 가장 성숙함.
- `ST_DWithin`(반경 검색), `ST_Distance`(거리 계산) 등 3단계 로드맵 "출근지 이동시간" 기능에 직접 활용 가능.
- MySQL의 Spatial 기능은 PostGIS 대비 제한적이고, MongoDB는 트랜잭션 관리가 복잡함.

### Redis
- 공공 API(청약·분양 정보) 응답 캐싱용. 외부 API 호출 횟수 제한 및 응답속도 개선.
- Spring Data Redis와 통합이 간단함.

### React Native
- iOS·Android 동시 대응을 단일 코드베이스로 처리.
- 지도 라이브러리(react-native-maps 등) 생태계가 충분히 성숙함.

### Docker + AWS ECS
- Docker로 로컬 개발 환경과 프로덕션 환경을 동일하게 유지.
- ECS(Fargate)는 Kubernetes 대비 운영 오버헤드가 낮고, AWS 생태계(RDS, ElastiCache, S3)와 자연스럽게 연결됨.

---

## 미결 사항

- Redis 도입 시점: 현재 1단계에서는 Redis 미사용. 4단계(공공 API) 도입 전 추가 예정.
- React Native 지도 라이브러리 최종 선택(react-native-maps vs Naver Map SDK)은 FE에서 결정.
