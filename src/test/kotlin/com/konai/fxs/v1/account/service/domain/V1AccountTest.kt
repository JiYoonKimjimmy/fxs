package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.testsupport.CustomStringSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

class V1AccountTest : CustomStringSpec({

    fun calculate(
        averageExchangeRate: BigDecimal,
        depositQuantity: BigDecimal,
        exchangeRate: BigDecimal,
        quantity: BigDecimal
    ): BigDecimal {
        return ((averageExchangeRate * depositQuantity) + (exchangeRate * quantity)) / (depositQuantity + quantity)
    }

    "외화 계좌 평단가 계산 정상 확인한다" {
        // given
        val averageExchangeRate = BigDecimal(0)
        val depositQuantity = BigDecimal(0)
        val exchangeRate = BigDecimal(1000)
        val quantity = BigDecimal(1)

        // when
        val result = calculate(averageExchangeRate, depositQuantity, exchangeRate, quantity)

        // then
        result shouldBe BigDecimal(1000)
    }

    "외화 계좌 평단가 계산하여 '1000.0' 변경 정상 확인한다" {
        // given
        val averageExchangeRate = BigDecimal(1000.0)
        val depositQuantity = BigDecimal(1)
        val exchangeRate = BigDecimal(1000.0)
        val quantity = BigDecimal(100)

        // when
        val result = calculate(averageExchangeRate, depositQuantity, exchangeRate, quantity)

        // then
        result shouldBe BigDecimal(1000.0)
    }

})