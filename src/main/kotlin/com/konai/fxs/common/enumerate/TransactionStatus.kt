package com.konai.fxs.common.enumerate

enum class TransactionStatus(private val note: String) {

    CREATED("거래 생성 상태"),
    PENDING("거래 대기 상태"),
    COMPLETED("거래 완료 상태"),
    EXPIRED("거래 만료 상태"),
    CANCELED("취소 거래 상태")

}