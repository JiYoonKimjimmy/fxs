package com.konai.fxs.common.enumerate

import com.konai.fxs.common.enumerate.TransactionCacheType.PENDING_TRANSACTION_AMOUNT_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.PENDING_TRANSACTION_CACHE
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import io.kotest.matchers.shouldBe

class TransactionCacheTypeTest : CustomStringSpec({

    "'보류 거래 Cache 정보' Cache Key 생성 정상 확인한다" {
        // given
        val trReferenceId = generateUUID()
        val channel = TransactionChannel.ORS.name

        // when
        val result = PENDING_TRANSACTION_CACHE.getKey(trReferenceId, channel)

        // then
        result shouldBe "fxs:$trReferenceId:$channel:pending:transaction"
    }

    "'보류 거래 금액 Cache 정보' Cache Key 생성 정상 확인한다" {
        // given
        val acquirerId = generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT.name

        // when
        val result = PENDING_TRANSACTION_AMOUNT_CACHE.getKey(acquirerId, acquirerType)

        // then
        result shouldBe "fxs:$acquirerId:$acquirerType:pending:transaction:amount"
    }

})