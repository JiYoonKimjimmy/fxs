package com.konai.fxs.common.enumerate

enum class DistributedLockType(
    private val note: String,
    private val keySpec: String
) {

    SEQUENCE_LOCK(
        note = "Sequence 생성 Lock",
        keySpec = "fxs:sequence:{sequenceType}:lock"
    )
    ,
    ACCOUNT_LOCK(
        note = "외화 계좌 정보 변경 Lock",
        keySpec = "fxs:account:{accountId}:lock"
    ),
    PREPARE_WITHDRAWAL_TRANSACTION_LOCK(
        note = "외화 계좌 출금 준비 금액 합계 변경 Lock",
        keySpec = "fxs:prepare:withdrawal:transaction:{accountId}:lock"
    );

    fun getKey(vararg args: String): String {
        return when(this) {
            SEQUENCE_LOCK -> {
                this.keySpec.replace("{sequenceType}", args[0])
            }
            ACCOUNT_LOCK -> {
                this.keySpec.replace("{accountId}", args[0])
            }
            PREPARE_WITHDRAWAL_TRANSACTION_LOCK -> {
                this.keySpec.replace("{accountId}", args[0])
            }
        }
    }

}