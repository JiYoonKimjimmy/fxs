package com.konai.fxs.common.restclient.knotify

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatusCode

@SpringBootTest
class KnotifyRestClientTest @Autowired constructor(
    private val knotifyRestClient: KnotifyRestClient
): StringSpec({

    "외화 계좌 잔액 부족 SMS 문자 발송 요청 성공한다" {
        // given
        val request = KnotifyPostSendSmsRequest.ofWarningBalanceInsufficient("01012341234")

        // when
        val response = knotifyRestClient.sendSMS(request)

        // then
        response.statusCode shouldBe HttpStatusCode.valueOf(200)
    }


})