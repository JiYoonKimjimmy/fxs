package com.konai.fxs.v1.exchangerate.koreaexim.service.domain

data class KoreaeximExchangeRate(
    val result: Int,
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