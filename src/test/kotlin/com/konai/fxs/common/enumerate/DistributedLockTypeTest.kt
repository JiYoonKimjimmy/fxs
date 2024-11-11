package com.konai.fxs.common.enumerate

import com.konai.fxs.common.enumerate.DistributedLockType.ACCOUNT_LOCK
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import io.kotest.matchers.shouldBe

class DistributedLockTypeTest : CustomStringSpec({

    "'acquirer' 기준 외화 계좌 입금 Distributed Lock Key 생성 정상 확인한다" {
        // given
        val accountId = generateUUID()

        // when
        val result = ACCOUNT_LOCK.getKey(accountId)

        // then
        result shouldBe "fxs:account:$accountId:lock"
    }

})