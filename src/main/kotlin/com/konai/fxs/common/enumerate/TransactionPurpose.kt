package com.konai.fxs.common.enumerate

enum class TransactionPurpose(private val note: String) {
    
    UNKNOWN("미정의 거래 목적"),
    DEPOSIT("단순 입금"),
    WITHDRAWAL("단순 출금"),
    FUNDING("펀딩"),
    REMITTANCE("송금")

}