package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.testsupport.CustomStringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.RoundingMode

class V1AccountTest : CustomStringSpec({

    fun calculate(
        averageExchangeRate: BigDecimal,
        depositAmount: BigDecimal,
        exchangeRate: BigDecimal,
        amount: BigDecimal
    ): BigDecimal {
        val averagePart = averageExchangeRate.multiply(depositAmount)
        val exchangePart = exchangeRate.multiply(amount)
        val numerator = averagePart.add(exchangePart)
        val denominator = depositAmount.add(amount)
        val result = numerator.divide(denominator, 2, RoundingMode.HALF_UP)
        return result
    }

    "외화 계좌 평단가 계산 정상 확인한다" {
        // given
        val averageExchangeRate = BigDecimal(0)
        val depositAmount = BigDecimal(0)
        val exchangeRate = BigDecimal(1000)
        val amount = BigDecimal(1)

        // when
        val result = calculate(averageExchangeRate, depositAmount, exchangeRate, amount)

        // then
        result.toDouble() shouldBe 1000.00
    }

    "외화 계좌 평단가 계산하여 '1000.0' 변경 정상 확인한다" {
        // given
        val averageExchangeRate = BigDecimal(1000.0)
        val depositAmount = BigDecimal(1)
        val exchangeRate = BigDecimal(1200.0)
        val amount = BigDecimal(1)

        // when
        val result = calculate(averageExchangeRate, depositAmount, exchangeRate, amount)

        // then
        result.toDouble() shouldBe 1100.00
    }

    "외화 계좌 평단가 계산하여 '867.86' 변경 정상 확인한다" {
        // given
        val averageExchangeRate = BigDecimal(850)
        val depositAmount = BigDecimal(450)
        val exchangeRate = BigDecimal(900)
        val amount = BigDecimal(250)

        // when
        val result = calculate(averageExchangeRate, depositAmount, exchangeRate, amount)

        // then
        result.toDouble() shouldBe 867.86
    }

})