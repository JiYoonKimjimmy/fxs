package com.konai.fxs.common.enumerate

enum class TransactionCacheType(
    private val note: String,
    private val keySpec: String
) {

    WITHDRAWAL_TRANSACTION_CACHE(
        note = "출금 거래 Cache 정보",
        keySpec = "fxs:{trReferenceId}:{channel}:withdrawal:transaction"
    ),
    WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE(
        note = "출금 거래 대기 금액 Cache 정보",
        keySpec = "fxs:{acquirerId}:{acquirerType}:withdrawal:transaction:pending:amount"
    )
    ;

    fun getKey(vararg args: String): String {
        return when (this) {
            WITHDRAWAL_TRANSACTION_CACHE -> {
                this.keySpec
                    .replace("{trReferenceId}", args[0])
                    .replace("{channel}", args[1])
            }
            WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE -> {
                this.keySpec
                    .replace("{acquirerId}", args[0])
                    .replace("{acquirerType}", args[1])
            }
        }
    }

}