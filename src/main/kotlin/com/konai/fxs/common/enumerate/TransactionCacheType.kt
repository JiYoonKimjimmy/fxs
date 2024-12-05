package com.konai.fxs.common.enumerate

enum class TransactionCacheType(
    private val note: String,
    private val keySpec: String
) {

    PENDING_TRANSACTION_CACHE(
        note = "보류 거래 Cache 정보",
        keySpec = "fxs:{trReferenceId}:{channel}:pending:transaction"
    ),
    PENDING_TRANSACTION_AMOUNT_CACHE(
        note = "보류 거래 금액 Cache 정보",
        keySpec = "fxs:{acquirerId}:{acquirerType}:pending:transaction:amount"
    )
    ;

    fun getKey(vararg args: String): String {
        return when(this) {
            PENDING_TRANSACTION_CACHE -> {
                this.keySpec
                    .replace("{trReferenceId}", args[0])
                    .replace("{channel}", args[1])
            }
            PENDING_TRANSACTION_AMOUNT_CACHE -> {
                this.keySpec
                    .replace("{acquirerId}", args[0])
                    .replace("{acquirerType}", args[1])
            }
        }
    }

}