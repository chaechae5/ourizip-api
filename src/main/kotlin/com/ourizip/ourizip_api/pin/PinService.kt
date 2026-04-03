package com.ourizip.ourizip_api.pin

import com.ourizip.ourizip_api.pin.dto.CreatePinRequest
import com.ourizip.ourizip_api.pin.dto.PinResponse
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PinService(
    private val pinRepository: PinRepository,
) {
    // SRID 4326 고정. GeometryFactory는 스레드 안전하므로 싱글턴으로 재사용.
    private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

    @Transactional
    fun createPin(userId: Long, request: CreatePinRequest): PinResponse {
        // JTS Coordinate(x, y) = (longitude, latitude) — 순서 주의
        val location = geometryFactory.createPoint(
            Coordinate(request.longitude, request.latitude)
        )

        val pin = Pin(
            userId = userId,
            location = location,
            comment = request.comment,
            categories = request.categories.toMutableList(),
            photos = request.photos.toMutableList(),
        )

        return PinResponse.from(pinRepository.save(pin))
    }
}
