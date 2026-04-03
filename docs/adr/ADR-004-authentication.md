# ADR-004: 인증 방식 — JWT + OAuth2 (카카오·구글)

- **날짜**: 2026-04-03
- **상태**: 채택됨 / 미구현 (2단계 전 구현 예정)

---

## 배경

모바일 앱(React Native) 사용자의 인증 방식을 결정해야 했다.
임장 앱 특성상 사용자는 주로 카카오·구글 계정을 이미 보유하고 있으며,
별도 이메일 회원가입 마찰을 최소화하는 것이 중요하다.

---

## 결정

**소셜 로그인(카카오·구글) OAuth2 → 서버에서 JWT 발급**

```
[클라이언트]
  ↓ 소셜 로그인 (카카오/구글 SDK)
  ↓ OAuth2 인가 코드 or Access Token 수신
[서버 POST /api/v1/auth/login]
  ↓ 소셜 Access Token 검증 (카카오·구글 API 호출)
  ↓ 사용자 조회 or 신규 생성
  ↓ 자체 JWT (Access Token + Refresh Token) 발급
[클라이언트]
  ↓ 이후 API 요청 시 Authorization: Bearer {accessToken}
```

---

## 선택지 비교

| 방식 | 장점 | 단점 |
|---|---|---|
| **JWT + 카카오·구글 OAuth2 (채택)** | 별도 회원가입 불필요, 모바일 친화적, 서버 무상태 | Refresh Token 관리 필요, 토큰 탈취 시 즉시 무효화 어려움 |
| Session 기반 | 즉시 로그아웃 가능 | 서버 무상태 불가 → ECS 수평 확장 시 세션 공유 문제 |
| Firebase Auth | 구현 단순 | 외부 서비스 종속, 커스텀 사용자 속성 관리 복잡 |
| 이메일/패스워드 자체 인증 | 완전 자체 통제 | 사용자 마찰, 비밀번호 분실 처리, 보안 부담 |

---

## 결정 이유

- **카카오**: 한국 부동산 앱 타겟 사용자의 카카오 계정 보유율이 높음.
- **구글**: 글로벌 폴백, Android 사용자 친화적.
- **JWT**: ECS Fargate 수평 확장 시 세션 스토리지 없이도 인증 가능.
- **서버 측 JWT 발급**: 클라이언트가 소셜 토큰을 직접 서버에 전달하지 않음. 소셜 API 호출은 서버에서만 처리해 토큰 노출 최소화.

---

## JWT 토큰 설계

| 항목 | 값 |
|---|---|
| Access Token 만료 | 1시간 |
| Refresh Token 만료 | 30일 |
| Refresh Token 저장 | Redis (서버 측) — 탈취 시 무효화 가능하도록 |
| 서명 알고리즘 | HS256 (초기), RS256 (프로덕션 고려) |

---

## 현재 상태 (1단계 MVP)

Spring Security가 의존성에는 포함되어 있으나 `SecurityConfig`에서 모든 요청을 허용 중.
`userId`는 임시로 `@RequestHeader("X-User-Id")`로 수신.

**교체 경로**: JWT 필터 구현 → `SecurityContext`에 `userId` 주입 → `@RequestHeader` 제거 → `@AuthenticationPrincipal`로 교체.

---

## 미결 사항

- Refresh Token Redis 키 설계 (`refresh:{userId}` vs `refresh:{tokenId}`)
- 카카오 로그인 앱 등록 및 Redirect URI 설정
- 다중 기기 로그인 허용 여부 (Refresh Token 1인 1토큰 vs 기기별)
