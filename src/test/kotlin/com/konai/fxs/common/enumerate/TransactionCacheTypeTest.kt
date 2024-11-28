package com.konai.fxs.common.enumerate

import com.konai.fxs.common.enumerate.TransactionCacheType.PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import io.kotest.matchers.shouldBe

class TransactionCacheTypeTest : CustomStringSpec({

    "'acquirer' 기준 출금 준비 합계 Cache Key 생성 정상 확인한다" {
        // given
        val acquirerId = generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT.name

        // when
        val result = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType)

        // then
        result shouldBe "fxs:${acquirerId}:${acquirerType}:prepared:withdrawal:total:amount"
    }

})