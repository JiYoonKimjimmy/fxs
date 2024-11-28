package com.konai.fxs.common.enumerate

enum class TransactionCacheType(
    private val note: String,
    private val keySpec: String
) {

    PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE(
        note = "출금 준비 합계 Cache 정보",
        keySpec = "fxs:{acquirerId}:{acquirerType}:prepared:withdrawal:total:amount"
    )
    ;

    fun getKey(vararg args: String): String {
        return when(this) {
            PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE -> {
                this.keySpec.replace("{acquirerId}", args[0]).replace("{acquirerType}", args[1])
            }
        }
    }

}