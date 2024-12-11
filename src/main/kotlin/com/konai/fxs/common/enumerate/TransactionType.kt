package com.konai.fxs.common.enumerate

enum class TransactionType(private val note: String) {
    
    DEPOSIT("입금"),
    WITHDRAWAL("출금")
    ;

    fun reverseType(): TransactionType {
        return when (this) {
            DEPOSIT -> WITHDRAWAL
            WITHDRAWAL -> DEPOSIT
        }
    }

}