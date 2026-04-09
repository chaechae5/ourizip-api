package com.ourizip.ourizip_api.common

import java.time.LocalDateTime

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
