package com.ourizip.ourizip_api.pin

import com.ourizip.ourizip_api.pin.dto.CreatePinRequest
import com.ourizip.ourizip_api.pin.dto.PinResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/pins")
class PinController(
    private val pinService: PinService,
) {
    /**
     * 핀 생성
     *
     * TODO: JWT 구현 후 @RequestHeader("X-User-Id")를 제거하고
     *       Authentication principal에서 userId를 추출할 것.
     *       현재는 헤더로 임시 수신 (Spring Security 미설정 상태).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPin(
        @RequestHeader("X-User-Id") userId: Long,
        @RequestBody @Valid request: CreatePinRequest,
    ): PinResponse = pinService.createPin(userId, request)
}
