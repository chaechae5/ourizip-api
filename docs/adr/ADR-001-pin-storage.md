# ADR-001: 핀(Pin) 저장 구조 설계

- **날짜**: 2026-04-03
- **상태**: 채택됨
- **작성자**: 초기 설계 시점 기록

---

## 1. 배경

임장 앱의 핵심 기능인 지도 핀 저장 API를 설계하면서 아래 세 가지 영역에서 의사결정이 필요했다.

1. 좌표 저장 방식 (PostGIS)
2. categories / photos 다중 값 저장 방식
3. userId 수신 방식 (인증 미구현 상태)

---

## 2. 의사결정 1 — PostGIS 좌표 저장: `geometry(Point,4326)` + JTS

### 선택지
| 방식 | 장점 | 단점 |
|---|---|---|
| **`geometry(Point,4326)` + Hibernate Spatial (채택)** | 거리 계산·반경 검색 등 공간 쿼리 네이티브 지원, 표준 GIS 생태계 | 설정 복잡도 약간 높음 |
| `DOUBLE PRECISION latitude/longitude` 분리 저장 | 구현 단순 | 거리 계산 시 하버사인 공식 직접 구현 필요, 인덱스 효율 낮음 |
| `VARCHAR` JSON 직렬화 | 스키마 변경 자유 | 공간 쿼리 불가 |

### 결정 이유
- **3단계 로드맵**에 "출근지 이동시간" 기능이 있어 반경 검색(`ST_DWithin`)이 필수가 됨.
- 나중에 `DOUBLE PRECISION`에서 PostGIS로 마이그레이션하면 데이터 이전 비용이 큼.
- Hibernate 6+ (Spring Boot 3+)에서 `hibernate-spatial`이 JTS `Point` ↔ PostGIS `geometry` 변환을 자동으로 처리하므로 추가 보일러플레이트 없음.

### 구현 세부 사항
- SRID `4326` = WGS84 = GPS 표준 좌표계. 앱이 수신하는 위·경도와 동일.
- `GeometryFactory(PrecisionModel(), 4326)`를 서비스 레이어 싱글턴으로 유지 (스레드 안전).
- JTS `Coordinate(x, y)` = `(longitude, latitude)` 순서 ← 직관과 반대라 주석으로 명시.

---

## 3. 의사결정 2 — categories / photos: `@ElementCollection` (별도 테이블)

### 선택지
| 방식 | 장점 | 단점 |
|---|---|---|
| **`@ElementCollection` (채택)** | 순수 JPA, 추가 의존성 없음, 정규화 | `pins` 조회 시 조인 발생 |
| PostgreSQL `text[]` 배열 + Hypersistence Utils | 단일 테이블 쿼리 | 라이브러리 추가 의존성, Hibernate 버전 호환성 확인 필요 |
| JSON 컬럼 (`jsonb`) | 유연한 구조 | 타입 검증·인덱싱 복잡 |
| 별도 엔티티 (`PinCategory`, `PinPhoto`) | 관계 명시, 추가 필드 확장 용이 | MVP 과도 설계 |

### 결정 이유
- MVP 단계에서 `text[]`용 Hypersistence Utils 의존성 추가보다 표준 JPA가 유지보수에 유리.
- `FetchType.EAGER`로 설정해 핀 조회 시 항상 함께 로드 — 핀 단건 조회 시 N+1 없음.
- 카테고리 마스터 테이블 미도입: MVP에서는 프론트에서 허용 값을 관리. 향후 `category` 엔티티 분리 가능.

---

## 4. 의사결정 3 — userId: `@RequestHeader("X-User-Id")` 임시 수신

### 배경
1단계 MVP에서는 JWT 인증이 미구현이지만, 핀은 사용자와 연결되어야 함.

### 선택지
| 방식 | 장점 | 단점 |
|---|---|---|
| `@RequestBody`에 userId 포함 | 간단 | 클라이언트가 타인 userId 위조 가능 — 보안 안티패턴 |
| **`@RequestHeader("X-User-Id")` (채택)** | 인증 레이어와 관심사 분리, 나중에 서블릿 필터로 주입 가능 | 인증 없으면 여전히 위조 가능 |
| 하드코딩 `userId = 1L` | 코드 단순 | 테스트 다중 사용자 불가 |

### 결정 이유
- 요청 바디에 userId를 넣으면 JWT 도입 시 API 스펙 변경이 필요함. 헤더 방식은 교체 범위가 `SecurityConfig` + 필터 레이어로 제한됨.
- JWT 구현 시 교체 경로: `@RequestHeader` 제거 → `@AuthenticationPrincipal UserDetails` 또는 `Authentication`에서 추출.

---

## 5. 의사결정 4 — `ddl-auto: update` (개발 환경)

### 결정 이유
- MVP 속도를 위해 Flyway 마이그레이션 도입을 생략.
- **프로덕션 이전 반드시 Flyway로 전환**해야 함 (`ddl-auto: validate`).
- `update` 모드는 컬럼 삭제를 자동으로 하지 않으므로 개발 환경에서 허용 가능한 수준.

---

## 6. 미결 사항 / TODO

| 항목 | 우선순위 |
|---|---|
| JWT 인증 구현 후 `@RequestHeader("X-User-Id")` → `@AuthenticationPrincipal` 교체 | 2단계 전 |
| Flyway 마이그레이션 스크립트 작성 | 프로덕션 이전 |
| S3 Presigned URL 업로드 플로우 구현 (현재는 URL 저장만) | 1단계 내 |
| 카테고리 허용 값 서버 측 enum 검증 추가 | 1단계 내 |
| `PostGIS` extension 활성화 확인: `CREATE EXTENSION IF NOT EXISTS postgis;` DB 초기화 스크립트 필요 | 즉시 |
