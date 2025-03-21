package com.konai.fxs.common.enumerate

enum class DistributedLockType(
    private val note: String,
    private val keySpec: String
) {

    SEQUENCE_LOCK(
        note = "Sequence 생성 Lock",
        keySpec = "fxs:{sequenceType}:sequence:lock"
    )
    ,
    ACCOUNT_LOCK(
        note = "외화 계좌 정보 변경 Lock",
        keySpec = "fxs:{accountId}:account:lock"
    ),
    WITHDRAWAL_TRANSACTION_AMOUNT_LOCK(
        note = "외화 계좌 출금 거래 대기 금액 변경 Lock",
        keySpec = "fxs:{accountId}:withdrawal:transaction:lock"
    ),
    EXCHANGE_RATE_COLLECTOR_TIMER_LOCK(
        note = "환율 정보 수집기 Timer Lock",
        keySpec = "fxs:{data}:exchange:rate:collect:timer"
    )
    ;

    fun getKey(vararg args: String): String {
        return when (this) {
            SEQUENCE_LOCK -> {
                this.keySpec.replace("{sequenceType}", args[0])
            }
            ACCOUNT_LOCK -> {
                this.keySpec.replace("{accountId}", args[0])
            }
            WITHDRAWAL_TRANSACTION_AMOUNT_LOCK -> {
                this.keySpec.replace("{accountId}", args[0])
            }
            EXCHANGE_RATE_COLLECTOR_TIMER_LOCK -> {
                this.keySpec.replace("{data}", args[0])
            }
        }
    }

}