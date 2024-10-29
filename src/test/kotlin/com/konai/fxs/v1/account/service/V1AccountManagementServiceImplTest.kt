package com.konai.fxs.v1.account.service

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestExtensionFunctions
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class V1AccountManagementServiceImplTest : CustomBehaviorSpec({

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1AccountManagementService = dependencies.v1AccountManagementService

    lateinit var saved: V1Account

    beforeSpec {
        val acquirerId = TestExtensionFunctions.generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val acquirer = V1Acquirer(id = acquirerId, type = acquirerType, name = acquirerName)
        val domain = v1AccountFixture.make(acquirer = acquirer)
        saved = v1AccountManagementService.create(domain)
    }

    given("외화 계좌 정보 저장 요청되어") {
        val acquirer = saved.acquirer

        `when`("이미 동일한 'acquirer' 정보 존재하는 경우") {
            val domain = v1AccountFixture.make(acquirer = acquirer)
            val exception = shouldThrow<InternalServiceException> { v1AccountManagementService.create(domain) }

            then("'ACCOUNT_ACQUIRER_IS_DUPLICATED' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_ACQUIRER_IS_DUPLICATED
            }
        }

        val acquirerId = TestExtensionFunctions.generateUUID()
        val acquirerType = AcquirerType.FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val newAcquirer = V1Acquirer(acquirerId, acquirerType, acquirerName)

        `when`("신규 등록 요청인 경우") {
            val domain = v1AccountFixture.make(acquirer = newAcquirer)
            val result = v1AccountManagementService.create(domain)

            then("저장 성공 결과 정상 확인한다") {
                result shouldNotBe null
                result.id shouldNotBe null
                result.acquirer.id shouldBe acquirerId
                result.acquirer.type shouldBe acquirerType
                result.acquirer.name shouldBe acquirerName
            }
        }
    }

    given("외화 계좌 정보 'id' 조건 조회 요청하여") {
        val id = 1234567890L
        var predicate = V1AccountPredicate(id = id)

        `when`("요청 'id' 정보와 일치한 정보 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1AccountManagementService.findByPredicate(predicate) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        predicate = V1AccountPredicate(id = saved.id!!)

        `when`("요청 'id' 정보와 일치한 정보 있는 경우") {
            val result = v1AccountManagementService.findByPredicate(predicate)

            then("조회 결과 성공 정상 확인한다") {
                result shouldNotBe null
                result.id shouldBe saved.id
                result.acquirer.id shouldBe saved.acquirer.id
                result.acquirer.type shouldBe saved.acquirer.type
                result.acquirer.name shouldBe saved.acquirer.name
            }
        }
    }

})