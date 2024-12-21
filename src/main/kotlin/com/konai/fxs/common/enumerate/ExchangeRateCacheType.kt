package com.konai.fxs.common.enumerate

enum class ExchangeRateCacheType(
    private val note: String,
    private val keySpec: String
) {

    KOREAEXIM_EXCHANGE_RATE_CACHE(
        note = "한국수출입은행 환율 Cache 정보",
        keySpec = "fxs:{currency}:koreaexim:exchange:rate"
    )
    ;

    fun getKey(vararg args: String): String {
        return when (this) {
            KOREAEXIM_EXCHANGE_RATE_CACHE -> {
                this.keySpec
                    .replace("{currency}", args[0])
            }
        }
    }

}