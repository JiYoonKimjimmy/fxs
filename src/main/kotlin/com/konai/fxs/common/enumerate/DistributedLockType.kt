package com.konai.fxs.common.enumerate

enum class DistributedLockType(
    private val note: String,
    private val keySpec: String
) {

    ACCOUNT_LOCK(
        note = "외화 계좌 정보 변경 Lock",
        keySpec = "fxs:account:{accountId}:lock")
    ;

    fun getKey(vararg args: String): String {
        return when(this) {
            ACCOUNT_LOCK -> {
                this.keySpec.replace("{accountId}", args[0])
            }
        }
    }

}