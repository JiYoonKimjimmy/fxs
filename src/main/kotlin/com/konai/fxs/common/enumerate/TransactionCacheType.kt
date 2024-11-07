package com.konai.fxs.common.enumerate

enum class TransactionCacheType(
    private val note: String,
    private val keySpec: String
) {

    WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE(
        note = "출금 준비 합계 Cache 정보",
        keySpec = "fxs:{acquirerId}:{acquirerType}:withdrawal:ready:total:amount"
    ),
    WITHDRAWAL_READY_TRANSACTIONS_CACHE(
        note = "출금 준비 거래 목록 Cache 정보",
        keySpec = "fxs:{acquirerId}:{acquirerType}:withdrawal:ready:transactions"
    )
    ;

    fun getKey(vararg args: String): String {
        return when(this) {
            WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE -> {
                this.keySpec.replace("{acquirerId}", args[0]).replace("{acquirerType}", args[1])
            }
            WITHDRAWAL_READY_TRANSACTIONS_CACHE -> {
                this.keySpec.replace("{acquirerId}", args[0]).replace("{acquirerType}", args[1])
            }
        }
    }

}