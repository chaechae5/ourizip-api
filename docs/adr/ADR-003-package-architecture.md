# ADR-003: 패키지 구조 — 도메인별 레이어드 아키텍처

- **날짜**: 2026-04-03
- **상태**: 채택됨

---

## 배경

Spring Boot 프로젝트의 패키지 구조를 결정해야 했다.
크게 두 가지 축에서 선택이 필요했다.

1. **도메인 우선** vs **레이어 우선**
2. **레이어드** vs **헥사고날(포트&어댑터)**

---

## 결정

**도메인별 패키지 + 레이어드 아키텍처**

```
com.ourizip.ourizip_api/
  pin/
    Pin.kt              ← Entity
    PinRepository.kt    ← Repository (Data 레이어)
    PinService.kt       ← Service (Business 레이어)
    PinController.kt    ← Controller (Presentation 레이어)
    dto/
      CreatePinRequest.kt
      PinResponse.kt
  region/
  notification/
  config/
  common/
    ErrorResponse.kt
    GlobalExceptionHandler.kt
```

---

## 선택지 비교

### 레이어 우선 구조
```
controller/PinController.kt
service/PinService.kt
repository/PinRepository.kt
entity/Pin.kt
```
- 장점: 레이어별 일관성 파악 쉬움
- 단점: 도메인 하나를 수정할 때 여러 패키지를 오가야 함. 도메인이 늘어날수록 탐색 비용 증가.

### 도메인 우선 + 레이어드 (채택)
- 장점: 도메인 단위로 응집도 높음. 한 도메인의 변경이 해당 패키지 내에서 완결됨.
- 단점: 레이어 간 의존 방향을 개발자가 직접 관리해야 함 (프레임워크 강제 없음).

### 헥사고날 아키텍처
- 장점: 포트·어댑터 분리로 테스트 용이성 극대화
- 단점: MVP 규모에서 과도한 추상화. 패키지 수 급증, 인터페이스 보일러플레이트.

---

## 결정 이유

- **현재 팀 규모와 도메인 복잡도**에서 헥사고날은 오버엔지니어링.
- 도메인 우선 구조는 `pin/` 하나만 보면 해당 기능의 전체 흐름을 파악할 수 있어 온보딩에 유리.
- 레이어드 의존 방향 규칙(`Controller → Service → Repository`)을 컨벤션으로 관리.

---

## 레이어 간 의존 규칙

```
PinController  →  PinService  →  PinRepository
     ↓                ↓
  dto/*.kt         Pin.kt (Entity)
```

- Controller는 Service만 호출. Repository 직접 호출 금지.
- Service는 도메인 로직 담당. HTTP 관련 코드(`HttpServletRequest` 등) 유입 금지.
- Entity는 레이어 경계를 넘지 않음 — Controller 응답은 반드시 DTO로 변환.
