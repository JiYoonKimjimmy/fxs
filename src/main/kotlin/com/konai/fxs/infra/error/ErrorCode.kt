package com.konai.fxs.infra.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "Account not found"),
    ACCOUNT_BALANCE_INSUFFICIENT(HttpStatus.INTERNAL_SERVER_ERROR, "002", "Account balance is insufficient"),
    
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "900", "Internal server error"),

    EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "901", "External API service error"),
    EXTERNAL_URL_PROPERTY_NOT_DEFINED(HttpStatus.INTERNAL_SERVER_ERROR, "902", "External url property is not defined"),
    EXTERNAL_SERVICE_RESPONSE_IS_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "903", "External API response is null"),

    UNKNOWN_ENVIRONMENT(HttpStatus.INTERNAL_SERVER_ERROR, "904", "Unknown environment of project"),
    ARGUMENT_NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "905", "Argument not valid"),

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "999", "Unknown error"),

}
