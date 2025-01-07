package com.konai.fxs.v1.exchangerate.controller.model

data class V1KoreaeximExchangeRateModel(
    val currency: String,
    val currencyName: String,
    val ttBuyRate: Double,
    val ttSellRate: Double,
    val dealCriteriaRate: Double
)