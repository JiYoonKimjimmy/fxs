package com.konai.fxs.infra.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "Account not found"),
    ACCOUNT_ACQUIRER_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "002", "Account's acquirer is duplicated"),
    ACCOUNT_STATUS_IS_DELETED(HttpStatus.BAD_REQUEST, "003", "Account status is already deleted"),
    ACCOUNT_STATUS_IS_INVALID(HttpStatus.BAD_REQUEST, "004", "Account status is invalid"),
    ACCOUNT_BALANCE_IS_INSUFFICIENT(HttpStatus.INTERNAL_SERVER_ERROR, "005", "Account balance is insufficient"),

    WITHDRAWAL_TRANSACTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "006", "Withdrawal transaction not found"),
    WITHDRAWAL_COMPLETED_TRANSACTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "007", "Withdrawal completed transaction not found"),
    WITHDRAWAL_TRANSACTION_IS_COMPLETED(HttpStatus.BAD_REQUEST, "008", "Withdrawal transaction is already completed"),

    KOREAEXIM_API_TYPE_IS_INVALID(HttpStatus.BAD_REQUEST, "009", "Koreaexim API type is invalid"),
    KOREAEXIM_API_KEY_IS_INVALID(HttpStatus.BAD_REQUEST, "010", "Koreaexim API key is invalid"),
    KOREAEXIM_API_REQUEST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "011", "Koreaexim API request limit exceeded"),
    KOREAEXIM_API_UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "012", "Koreaexim API unknown error"),

    EXCHANGE_RATE_NOT_FOUND(HttpStatus.NOT_FOUND, "013", "Exchange rate not found"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "900", "Internal server error"),
    EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "901", "External API service error"),
    EXTERNAL_URL_PROPERTY_NOT_DEFINED(HttpStatus.INTERNAL_SERVER_ERROR, "902", "External url property is not defined"),
    UNKNOWN_ENVIRONMENT(HttpStatus.INTERNAL_SERVER_ERROR, "904", "Unknown environment of project"),
    ARGUMENT_NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "905", "Argument not valid"),
    CACHE_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "906", "Cache service error"),
    REDISSON_LOCK_ATTEMPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "907", "Redisson lock attempt error"),

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "999", "Unknown error"),

}
