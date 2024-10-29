package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

class V1UpdateAccountRequestTest : CustomBehaviorSpec({

    given("외화 계좌 변경 요청 정보 생성하여") {
        val accountId = generateSequence()
        val acquirerId = generateUUID()

        `when`("'acquirerId' 정보는 있지만, 'acquirerType' 정보가 없는 경우") {
            val request = V1UpdateAccountRequest(
                accountId = accountId,
                acquirerId = acquirerId,
                acquirerType = null,
                acquirerName = null,
                currency = null,
                minRequiredBalance = null,
            )
            val exception = shouldThrow<InternalServiceException> { request.validation() }

            then("'ARGUMENT_NOT_VALID_ERROR' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ARGUMENT_NOT_VALID_ERROR
            }
        }

        val acquirerType = AcquirerType.FX_DEPOSIT

        `when`("'acquirerId' & 'acquirerType' 정보는 있지만, 'acquirerName' 정보가 없는 경우") {
            val request = V1UpdateAccountRequest(
                accountId = accountId,
                acquirerId = acquirerId,
                acquirerType = acquirerType,
                acquirerName = null,
                currency = null,
                minRequiredBalance = null,
            )
            val exception = shouldThrow<InternalServiceException> { request.validation() }

            then("'ARGUMENT_NOT_VALID_ERROR' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ARGUMENT_NOT_VALID_ERROR
            }
        }

        val acquirerName = "외화 예치금 계좌"

        `when`("'acquirerId' & 'acquirerType' & 'acquirerName' 모두 있는 경우") {
            val request = V1UpdateAccountRequest(
                accountId = accountId,
                acquirerId = acquirerId,
                acquirerType = acquirerType,
                acquirerName = acquirerName,
                currency = null,
                minRequiredBalance = null,
            )
            val result = request.validation()

            then("정상 객체 생성 확인한다") {
                result.accountId shouldBe accountId
                result.acquirerId shouldBe acquirerId
                result.acquirerType shouldBe acquirerType
                result.acquirerName shouldBe acquirerName
            }
        }
    }

})