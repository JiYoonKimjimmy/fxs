package com.konai.fxs.common.external.knotify

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.konai.fxs.testsupport.CustomRestClientTest
import com.konai.fxs.testsupport.CustomStringSpec
import com.konasl.commonlib.springweb.correlation.core.RequestContext
import com.konasl.commonlib.springweb.correlation.loggercontext.CorrelationLoggingField.CORRELATION_ID_LOG_FIELD
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.slf4j.MDC
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException

@CustomRestClientTest
class KnotifyHttpServiceImplTest(
    private val knotifyHttpServiceProxy: KnotifyHttpServiceProxy
) : CustomStringSpec({

    val knotifyHttpService = KnotifyHttpServiceImpl(knotifyHttpServiceProxy)

    beforeTest {
        MDC.put(CORRELATION_ID_LOG_FIELD.getName(), RequestContext.generateId())
    }

    "KNOTIFY 컴포넌트 SMS 문자 발송 API 요청 성공 확인한다" {
        // given
        val request = KnotifyPostSendSmsRequest.ofWarningBalanceInsufficient("01012341234")

        // when
        val response = knotifyHttpService.sendSMS(request)

        // then
        response.statusCode shouldBe HttpStatusCode.valueOf(200)
    }

    "KNOTIFY 컴포넌트 SMS 문자 발송 API 요청 실패 확인한다" {
        // given
        val request = KnotifyPostSendSmsRequest(
            aspId = "953365000000000",
            id = "01012341234",
            idType = "MOBILE",
            notificationType = "CHATBOT_INFO_CHECK_TMP",
            templateVariables = mapOf("authCode" to "123456")
        )

        // when
        val exception = shouldThrow<HttpClientErrorException> { knotifyHttpService.sendSMS(request) }

        // then
        val responseBody = jacksonObjectMapper().registerModule(kotlinModule()).readValue<Map<String, String>>(exception.responseBodyAsString)
        responseBody["reason"] shouldBe "29_0006_013"
        responseBody["message"] shouldBe "Sms Sending. Message Template Not Found. Message template not found."

        exception.statusCode shouldBe HttpStatusCode.valueOf(404)
    }

})