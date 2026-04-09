# ADR-005: 사진 저장 방식 — S3 Presigned URL + URL만 DB 저장

- **날짜**: 2026-04-03
- **상태**: 채택됨 / S3 업로드 플로우 미구현 (1단계 내 구현 예정)

---

## 배경

핀에 사진을 첨부하는 기능을 설계하면서 업로드 방식과 저장 방식을 결정해야 했다.
임장 현장에서 찍은 사진은 용량이 크고, 모바일 네트워크 환경이 불안정할 수 있다.

---

## 결정

**클라이언트 → S3 직접 업로드 (Presigned URL) + 서버는 S3 URL만 DB에 저장**

```
[클라이언트]
  ↓ GET /api/v1/pins/upload-url?count=3  (업로드할 사진 수)
[서버]
  ↓ S3 Presigned PUT URL 생성 (만료: 5분)
  ↓ URL 목록 반환
[클라이언트]
  ↓ Presigned URL로 S3에 직접 PUT 업로드
  ↓ 업로드 완료 후 S3 URL 목록을 POST /api/v1/pins 바디에 포함
[서버]
  ↓ pin_photos 테이블에 URL만 저장
```

---

## 선택지 비교

| 방식 | 장점 | 단점 |
|---|---|---|
| **S3 Presigned URL (채택)** | 서버 대역폭 절약, 클라이언트가 S3에 직접 전송 | 클라이언트 구현 복잡도 약간 증가 |
| 서버 경유 업로드 (`multipart/form-data`) | 구현 단순, 서버에서 이미지 검증·리사이징 가능 | 서버 메모리·대역폭 부담, ECS 인스턴스 크기에 영향 |
| 클라이언트 → CloudFront → S3 | CDN 캐싱으로 조회 성능 향상 | 초기 설정 복잡 |

---

## 결정 이유

- **서버 대역폭**: 사진 파일이 서버를 거치지 않으므로 ECS 인스턴스 I/O 부담 없음.
- **확장성**: 업로드 트래픽이 늘어도 서버 스케일아웃 불필요.
- **보안**: Presigned URL은 만료 시간(5분) 이후 사용 불가. 버킷을 퍼블릭으로 열지 않아도 됨.
- **서버는 URL만 저장**: DB에 바이너리 저장 없이 `text` 컬럼에 S3 URL 문자열만 보관. 핀 조회 시 클라이언트가 직접 S3/CloudFront에서 이미지 로드.

---

## 이미지 처리 정책

| 항목 | 결정 |
|---|---|
| 업로드 전 리사이징 | 클라이언트 담당 (1MB 이하로 압축) — TODO.md 비기능 요구사항 참고 |
| 허용 확장자 검증 | Presigned URL 생성 시 `Content-Type` 제한 (`image/jpeg`, `image/png`, `image/webp`) |
| 최대 첨부 수 | 핀당 10장 (서비스 레이어 검증) |
| S3 버킷 경로 | `pins/{userId}/{pinId}/{uuid}.jpg` |

---

## 현재 상태 (1단계 MVP)

`POST /api/v1/pins` 요청 바디에 `photos: [S3 URL]` 배열을 직접 받아 DB에 저장.
Presigned URL 발급 엔드포인트(`GET /api/v1/pins/upload-url`)는 미구현.

**구현 예정**: S3 Presigned URL 발급 API + AWS SDK 설정.

---

## 미결 사항

- CloudFront 연동 여부 (조회 성능 및 비용 트레이드오프)
- 핀 삭제 시 S3 오브젝트 삭제 정책 (즉시 삭제 vs 배치 정리)
- 사진 순서 보장 여부 (현재 `List` 순서 의존)
