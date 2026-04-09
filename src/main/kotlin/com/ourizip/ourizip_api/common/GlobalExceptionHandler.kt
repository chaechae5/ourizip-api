package com.ourizip.ourizip_api.common

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: MethodArgumentNotValidException): ErrorResponse {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ErrorResponse(code = "VALIDATION_ERROR", message = message)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingHeader(ex: MissingRequestHeaderException): ErrorResponse =
        ErrorResponse(code = "MISSING_HEADER", message = "필수 헤더가 없습니다: ${ex.headerName}")

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ErrorResponse =
        ErrorResponse(code = "INVALID_PARAMETER", message = "잘못된 파라미터 값입니다: ${ex.name}")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ErrorResponse =
        ErrorResponse(code = "INVALID_REQUEST_BODY", message = "요청 본문을 읽을 수 없습니다")

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NoSuchElementException): ErrorResponse =
        ErrorResponse(code = "NOT_FOUND", message = ex.message ?: "리소스를 찾을 수 없습니다")

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(ex: Exception): ErrorResponse {
        log.error("Unhandled exception", ex)
        return ErrorResponse(code = "INTERNAL_ERROR", message = "서버 오류가 발생했습니다")
    }
}
