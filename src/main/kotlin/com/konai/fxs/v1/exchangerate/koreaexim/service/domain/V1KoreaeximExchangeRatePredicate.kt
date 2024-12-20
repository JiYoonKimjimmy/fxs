package com.konai.fxs.v1.exchangerate.koreaexim.service.domain

data class V1KoreaeximExchangeRatePredicate(
    val id: Long? = null,
    val registerDate: String? = null,
    val index: Int? = null,
    val result: Int? = null,
    val curUnit: String? = null,
    val curNm: String? = null,
    val ttb: String? = null,
    val tts: String? = null,
    val dealBasR: String? = null,
    val bkpr: String? = null,
    val yyEfeeR: String? = null,
    val tenDdEfeeR: String? = null,
    val kftcDealBasR: String? = null,
    val kftcBkpr: String? = null
)