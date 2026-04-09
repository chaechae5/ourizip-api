package com.ourizip.ourizip_api.pin.dto

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreatePinRequest(

    @field:NotNull
    @field:DecimalMin("-90.0")
    @field:DecimalMax("90.0")
    val latitude: Double,

    @field:NotNull
    @field:DecimalMin("-180.0")
    @field:DecimalMax("180.0")
    val longitude: Double,

    @field:Size(max = 2000)
    val comment: String? = null,

    @field:Size(max = 10, message = "카테고리는 최대 10개까지 지정할 수 있습니다")
    val categories: List<String> = emptyList(),

    @field:Size(max = 10, message = "사진은 최대 10장까지 첨부할 수 있습니다")
    val photos: List<String> = emptyList(),
)
