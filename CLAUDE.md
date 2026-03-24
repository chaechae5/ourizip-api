# CLAUDE.md

## 1. Project Overview
부동산 임장(현장방문) 기록을 지도 기반으로 관리하는 모바일·웹 앱. 
핵심은 지도 위 핀+메모, 출근지 이동시간 계산, '그때 샀다면?' 시세 시뮬레이터(바이럴 포인트), 청약·분양 정보 제공

## 2. Tech Stack
BE: Spring Boot 3 + Kotlin
DB: PostgreSQL 15 + PostGIS 
Auth: JWT + OAuth2 (카카오·구글)
Deploy: Docker + AWS ECS

## 3. Architecture Overview
React Native → REST API (JWT) → Spring Boot → PostgreSQL+PostGIS / Redis / S3
외부 API는 BE에서 프록시 처리. 공공 API 응답은 Redis 캐싱.

## 4. Development Roadmap
1단계(현재): 지도 UI + 핀·코멘트·사진 MVP  
2단계: 즐겨찾기·임장 타임라인  
3단계: 출근지 이동시간 + 시세 시뮬레이터 ★  
4단계: 청약·분양 오버레이 (공공API 승인 필요)  
5단계: 알림 고도화  
→ 지금은 1단계만 구현. 다른 단계 코드 먼저 짜지 말 것.

## 5. Project Structure
도메인별 패키지 + 레이어드 구조
pin/ Pin.kt, PinService.kt, PinController.kt, PinRepository.kt
region/
notification/

## 6. External APIs
API Key는 환경변수로만 관리, 코드에 하드코딩 절대 금지.

## 8. Code Conventions
Kotlin: PascalCase 클래스, camelCase 함수·변수, UPPER_SNAKE_CASE 상수, suspend 함수에서 블로킹 I/O 금지
REST: /api/v1/{resource} 복수형, 에러응답 { code, message, timestamp } 통일

## Reference Docs
상세 내용은 'docs/' 경로에 관련 문서 참고
- 'docs/api-spec.md' : API 엔드포인트, 스키마, enums
- 'docs/db-schema.md' : 테이블 정보
- 'docs/architecture.md' : 시스템 아키텍처 다이어그램