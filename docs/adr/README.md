# Architecture Decision Records (ADR)

프로젝트의 주요 기술 및 설계 결정을 기록합니다.
새로운 결정이 생기면 다음 번호로 파일을 추가하고 이 인덱스를 업데이트하세요.

---

## 인덱스

| ID | 제목 | 상태 | 날짜 |
|---|---|---|---|
| [ADR-001](./ADR-001-pin-storage.md) | 핀 저장 구조 (PostGIS, ElementCollection, userId 헤더, ddl-auto) | 채택됨 | 2026-04-03 |
| [ADR-002](./ADR-002-tech-stack.md) | 기술 스택 선택 (Kotlin/Spring Boot 3/PostgreSQL+PostGIS/ECS) | 채택됨 | 2026-04-03 |
| [ADR-003](./ADR-003-package-architecture.md) | 패키지 구조 — 도메인별 레이어드 아키텍처 | 채택됨 | 2026-04-03 |
| [ADR-004](./ADR-004-authentication.md) | 인증 방식 — JWT + OAuth2 (카카오·구글) | 채택됨 / 미구현 | 2026-04-03 |
| [ADR-005](./ADR-005-photo-storage.md) | 사진 저장 — S3 Presigned URL + URL만 DB 저장 | 채택됨 / 부분 구현 | 2026-04-03 |
| [ADR-006](./ADR-006-development-roadmap.md) | 단계별 개발 전략 — 1단계 MVP 우선 구현 | 채택됨 | 2026-04-03 |
| [ADR-007](./ADR-007-external-api-proxy.md) | 외부 API — 서버 프록시 + Redis 캐싱 | 채택됨 / 미구현 | 2026-04-03 |

---

## ADR 작성 가이드

```
# ADR-NNN: 제목

- 날짜:
- 상태: 채택됨 | 폐기됨 | 대체됨 (ADR-NNN에 의해)

## 배경
## 결정
## 이유
## 미결 사항
```
