package com.konai.fxs.common.enumerate

enum class TransactionCacheType(
    private val note: String,
    private val keySpec: String
) {

    PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE(
        note = "출금 준비 합계 Cache 정보",
        keySpec = "fxs:{acquirerId}:{acquirerType}:prepared:withdrawal:total:amount"
    ),
    PREPARED_WITHDRAWAL_TRANSACTION_CACHE(
        note = "출금 준비 거래 Cache 정보",
        keySpec = "fxs:{acquirerId}:{acquirerType}:prepared:withdrawal:transaction:{trReferenceId}"
    )
    ;

    fun getKey(vararg args: String): String {
        return when(this) {
            PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE -> {
                this.keySpec
                    .replace("{acquirerId}", args[0])
                    .replace("{acquirerType}", args[1])
            }
            PREPARED_WITHDRAWAL_TRANSACTION_CACHE -> {
                this.keySpec
                    .replace("{acquirerId}", args[0])
                    .replace("{acquirerType}", args[1])
                    .replace("{trReferenceId}", args[2])
            }
        }
    }

}