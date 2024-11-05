package com.konai.fxs.v1.account.service

import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

class V1AccountValidationServiceImplTest : CustomBehaviorSpec({

    val v1AccountValidationService = dependencies.v1AccountValidationService
    val v1AccountSaveService = dependencies.v1AccountSaveService
    val v1AccountFixture = dependencies.v1AccountFixture

    fun saveAccount(account: V1Account): V1Account {
        return v1AccountSaveService.save(account)
    }

    given("외화 계좌 상태 확인 요청하여") {
        val currency = "USD"

        `when`("'acquirer' 조건 일치한 외화 계좌 정보 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1AccountValidationService.checkStatus(V1Acquirer(generateUUID(), AcquirerType.FX_DEPOSIT), currency) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        val inActiveAccount = saveAccount(v1AccountFixture.make(status = AccountStatus.INACTIVE))

        `when`("'acquirer' 조건 일치한 외화 계좌 상태 'ACTIVE' 아닌 경우") {
            val exception = shouldThrow<InternalServiceException> { v1AccountValidationService.checkStatus(inActiveAccount.acquirer, inActiveAccount.currency) }

            then("'ACCOUNT_STATUS_IS_INVALID' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_STATUS_IS_INVALID
            }
        }

        val activeAccount = saveAccount(v1AccountFixture.make(status = AccountStatus.ACTIVE))
        
        `when`("'acquirer' 조건 일치한 외화 계좌 상태 'ACTIVE' 인 경우") {
            val result = v1AccountValidationService.checkStatus(activeAccount.acquirer, activeAccount.currency)
            
            then("예외 발생 없이 정상 확인한다") {
                result.id shouldBe activeAccount.id
                result.acquirer.id shouldBe activeAccount.acquirer.id
                result.status shouldBe AccountStatus.ACTIVE
            }
        }
    }

})