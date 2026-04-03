package com.ourizip.ourizip_api.pin

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

@Entity
@Table(name = "pins")
class Pin(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // TODO: JWT 구현 후 SecurityContext에서 추출. 현재는 헤더 수신 후 서비스에서 주입.
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    // PostGIS geometry(Point,4326) — SRID 4326 = WGS84 (GPS 표준 좌표계)
    // columnDefinition은 ddl-auto가 테이블을 생성할 때 올바른 PostGIS 타입을 쓰도록 명시.
    // Hibernate Spatial이 JTS Point ↔ geometry 변환을 자동으로 처리함.
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    val location: Point,

    @Column(columnDefinition = "text")
    val comment: String? = null,

    // 카테고리 목록 — 별도 pin_categories 테이블. 정규화 vs 단순성 트레이드오프:
    // MVP에서는 카테고리 마스터 테이블 없이 문자열 직접 저장. enum 검증은 서비스 레이어에서.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pin_categories", joinColumns = [JoinColumn(name = "pin_id")])
    @Column(name = "category", nullable = false)
    var categories: MutableList<String> = mutableListOf(),

    // S3 URL 목록 — 업로드 주체는 클라이언트(Presigned URL 방식). 서버는 URL만 저장.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pin_photos", joinColumns = [JoinColumn(name = "pin_id")])
    @Column(name = "photo_url", nullable = false)
    var photos: MutableList<String> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
