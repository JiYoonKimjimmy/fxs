package com.konai.fxs.common.enumerate

import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_CACHE
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import io.kotest.matchers.shouldBe

class TransactionCacheTypeTest : CustomStringSpec({

    "'출금 거래 Cache 정보' Cache Key 생성 정상 확인한다" {
        // given
        val trReferenceId = generateUUID()
        val channel = TransactionChannel.ORS.name

        // when
        val result = WITHDRAWAL_TRANSACTION_CACHE.getKey(trReferenceId, channel)

        // then
        result shouldBe "fxs:$trReferenceId:$channel:withdrawal:transaction"
    }

    "'출금 거래 대기 금액 Cache 정보' Cache Key 생성 정상 확인한다" {
        // given
        val acquirerId = generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT.name

        // when
        val result = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirerId, acquirerType)

        // then
        result shouldBe "fxs:$acquirerId:$acquirerType:withdrawal:transaction:pending:amount"
    }

})