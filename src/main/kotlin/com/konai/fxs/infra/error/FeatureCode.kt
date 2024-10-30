package com.konai.fxs.infra.error

enum class FeatureCode(
    val code: String,
    val message: String
) {

    UNKNOWN("9999", "Unknown Service"),

    V1_ACCOUNT_MANAGEMENT_SERVICE("1001", "Account management service"),
    V1_ACCOUNT_TRANSACTION_SERVICE("1002", "Account transaction service"),

}