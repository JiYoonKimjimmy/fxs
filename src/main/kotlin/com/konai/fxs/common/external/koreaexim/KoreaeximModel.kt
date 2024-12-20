package com.konai.fxs.common.external.koreaexim

import com.fasterxml.jackson.annotation.JsonProperty

data class KoreaeximGetExchangeRateResponse(
    @field:JsonProperty("result")          val result: Int,          // 조회 결과 (1: 성공, 2: DATA코드 오류, 3: 인증코드 오류, 4: 일일제한횟수 마감)
    @field:JsonProperty("cur_unit")        val curUnit: String,      // 통화코드
    @field:JsonProperty("cur_nm")          val curNm: String,        // 국가/통화명
    @field:JsonProperty("ttb")             val ttb: String,          // 전신환(송금)받으실때(매입환율)
    @field:JsonProperty("tts")             val tts: String,          // 전신환(송금)보내실때(매도환율)
    @field:JsonProperty("deal_bas_r")      val dealBasR: String,     // 매매기준율(기준환율)
    @field:JsonProperty("bkpr")            val bkpr: String,         // 장부가격
    @field:JsonProperty("yy_efee_r")       val yyEfeeR: String,      // 년환가료율(연간 수수료율)
    @field:JsonProperty("ten_dd_efee_r")   val tenDdEfeeR: String,   // 10일환가료율(10일 수수료율)
    @field:JsonProperty("kftc_deal_bas_r") val kftcDealBasR: String, // 서울외국환중개매매기준율
    @field:JsonProperty("kftc_bkpr")       val kftcBkpr: String      // 서울외국환중개 장부가격
)