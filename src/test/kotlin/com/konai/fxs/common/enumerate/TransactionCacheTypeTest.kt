package com.konai.fxs.common.enumerate

import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_READY_TRANSACTIONS_CACHE
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import io.kotest.matchers.shouldBe

class TransactionCacheTypeTest : CustomStringSpec({

    "'acquirer' 기준 출금 준비 합계 Cache Key 생성 정상 확인한다" {
        // given
        val acquirerId = generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT.name

        // when
        val result = WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType)

        // then
        result shouldBe "fxs:${acquirerId}:${acquirerType}:withdrawal:ready:total:amount"
    }

    "'acquirer' 기준 출금 거래 목록 Cache Key 생성 정상 확인한다" {
        // given
        val acquirerId = generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT.name

        // when
        val result = WITHDRAWAL_READY_TRANSACTIONS_CACHE.getKey(acquirerId, acquirerType)

        // then
        result shouldBe "fxs:${acquirerId}:${acquirerType}:withdrawal:ready:transactions"
    }

})