package com.konai.fxs.common.enumerate

enum class TransactionType(private val note: String) {
    
    DEPOSIT("입금"),
    WITHDRAWAL("출금")
    ;

    fun cancelType(): TransactionType {
        return when {
            this == DEPOSIT -> WITHDRAWAL
            this == WITHDRAWAL -> DEPOSIT
            else -> this
        }
    }

}