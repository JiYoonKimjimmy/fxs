package com.konai.fxs.v1.exchangerate.controller.model

data class V1KoreaeximExchangeRateModel(
    val curUnit: String,
    val curNm: String,
    val ttb: String,
    val tts: String,
    val dealBasR: String,
    val bkpr: String,
    val yyEfeeR: String,
    val tenDdEfeeR: String,
    val kftcDealBasR: String,
    val kftcBkpr: String
)