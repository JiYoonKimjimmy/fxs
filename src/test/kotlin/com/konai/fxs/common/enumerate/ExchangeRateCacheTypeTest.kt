package com.konai.fxs.common.enumerate

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.ExchangeRateCacheType.KOREAEXIM_EXCHANGE_RATE_CACHE
import com.konai.fxs.testsupport.CustomStringSpec
import io.kotest.matchers.shouldBe

class ExchangeRateCacheTypeTest : CustomStringSpec({

    "'한국수출입은행 환율 Cache 정보' Cache Key 생성 정상 확인한다" {
        // given
        val currency = Currency.USD

        // when
        val result = KOREAEXIM_EXCHANGE_RATE_CACHE.getKey(currency)

        // then
        result shouldBe "fxs:USD:koreaexim:exchange:rate"
    }

})