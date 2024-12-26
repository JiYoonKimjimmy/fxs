package com.konai.fxs.infra.error

enum class FeatureCode(
    val code: String,
    val message: String
) {

    UNKNOWN("9999", "Unknown Service"),

    V1_ACCOUNT_MANAGEMENT_SERVICE("1001", "Account management service"),
    V1_ACCOUNT_VALIDATION_SERVICE("1002", "Account validation service"),

    V1_ACCOUNT_TRANSACTION_SERVICE("2001", "Account transaction service"),

    V1_EXCHANGE_RATE_FIND_SERVICE("3001", "Exchange rate find service"),

}