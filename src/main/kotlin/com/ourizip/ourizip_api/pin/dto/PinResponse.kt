package com.ourizip.ourizip_api.pin.dto

import com.ourizip.ourizip_api.pin.Pin
import java.time.LocalDateTime

data class PinResponse(
    val id: Long,
    val userId: Long,
    val latitude: Double,
    val longitude: Double,
    val comment: String?,
    val categories: List<String>,
    val photos: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(pin: Pin) = PinResponse(
            id = pin.id,
            userId = pin.userId,
            // JTS Coordinate: x = longitude, y = latitude
            latitude = pin.location.y,
            longitude = pin.location.x,
            comment = pin.comment,
            categories = pin.categories.toList(),
            photos = pin.photos.toList(),
            createdAt = pin.createdAt,
            updatedAt = pin.updatedAt,
        )
    }
}
