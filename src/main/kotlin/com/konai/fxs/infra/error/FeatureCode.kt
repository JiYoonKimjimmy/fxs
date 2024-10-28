package com.konai.fxs.infra.error

enum class FeatureCode(
    val code: String,
    val message: String
) {

    UNKNOWN("9999", "Unknown Service"),

    V1_ACCOUNT_MANAGEMENT_SERVICE("1000", "Account management service"),
    TRANSACTION_SERVICE("2000", "Transaction service"),

}