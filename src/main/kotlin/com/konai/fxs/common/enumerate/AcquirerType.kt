package com.konai.fxs.common.enumerate

enum class AcquirerType(private val note: String) {
    
    FX_DEPOSIT("외화 예치금 계좌"),
    FX_PURCHASER("외화 매입처 계좌"),
    MTO_FUNDING("MTO 펀딩 계좌")

}