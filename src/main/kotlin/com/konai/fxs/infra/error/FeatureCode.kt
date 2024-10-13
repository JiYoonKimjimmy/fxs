package com.konai.fxs.infra.error

enum class FeatureCode(
    val code: String,
    val message: String
) {

    UNKNOWN("9999", "Unknown Service"),

    ACCOUNT_SERVICE("1000", "Account Service"),
    TRANSACTION_SERVICE("2000", "Transaction Service"),

}