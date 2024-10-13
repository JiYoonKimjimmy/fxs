package com.konai.fxs.common.enumerate

enum class BalanceCheckResult(private val note: String) {
    
    SUFFICIENT("잔액 충분"),
    WARNING("최소 유지 금액 도달 경고"),
    INSUFFICIENT("잔액 부족")
    
}