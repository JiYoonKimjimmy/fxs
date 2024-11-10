package com.konai.fxs.infra.error.handler

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.common.model.ErrorResponse
import com.konai.fxs.infra.error.FeatureCode
import com.konai.fxs.infra.error.exception.BaseException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.infra.error.exception.RestClientServiceException
import com.konai.fxs.common.EMPTY
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException

@RestControllerAdvice
class BaseExceptionHandler(
    private val featureCode: FeatureCode = FeatureCode.UNKNOWN
) {

    @ExceptionHandler(ResourceNotFoundException::class)
    protected fun handleResourceNotFoundException(e: BaseException): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(featureCode, e.errorCode)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = e.bindingResult.fieldErrors.joinToString(". ") { it.defaultMessage ?: EMPTY }
        return ErrorResponse.toResponseEntity(featureCode, ErrorCode.ARGUMENT_NOT_VALID_ERROR, message)
    }

    @ExceptionHandler(HttpClientErrorException::class)
    protected fun handleHttpClientErrorException(e: HttpClientErrorException): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(featureCode, ErrorCode.EXTERNAL_SERVICE_ERROR, e.message)
    }

    @ExceptionHandler(RestClientServiceException::class)
    protected fun handleRestClientServiceException(e: RestClientServiceException): ResponseEntity<ErrorResponse> {
        return try {
            ErrorResponse.toResponseEntity(e.detailMessage!!)
        } catch (e: Exception) {
            ErrorResponse.toResponseEntity(featureCode, ErrorCode.EXTERNAL_SERVICE_ERROR, e.message)
        }
    }

    @ExceptionHandler(BaseException::class)
    protected fun handleCustomException(e: BaseException): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(featureCode, e.errorCode, e.detailMessage)
    }

    @ExceptionHandler(Exception::class)
    protected fun exceptionHandler(e: Exception): ResponseEntity<ErrorResponse> {
        return ErrorResponse.toResponseEntity(featureCode, ErrorCode.UNKNOWN_ERROR, e.message)
    }

}