package com.konai.fxs.v1.account.service

import com.konai.fxs.common.enumerate.AccountStatus.*
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class V1AccountManagementServiceImplTest : CustomBehaviorSpec({

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1AccountManagementService = dependencies.v1AccountManagementService

    lateinit var saved: V1Account

    beforeSpec {
        val acquirerId = generateUUID()
        val acquirerType = FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val domain = v1AccountFixture.make(
            acquirerId = acquirerId,
            acquirerType = acquirerType,
            acquirerName = acquirerName
        )
        saved = v1AccountManagementService.save(domain)
    }

    given("외화 계좌 정보 저장 요청되어") {
        val acquirer = saved.acquirer

        `when`("이미 동일한 'acquirer' 정보 존재하는 경우") {
            val domain = v1AccountFixture.make(
                acquirerId = acquirer.id,
                acquirerType = acquirer.type,
                acquirerName = acquirer.name
            )
            val exception = shouldThrow<InternalServiceException> { v1AccountManagementService.save(domain) }

            then("'ACCOUNT_ACQUIRER_IS_DUPLICATED' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_ACQUIRER_IS_DUPLICATED
            }
        }

        val acquirerId = generateUUID()
        val acquirerType = FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"

        `when`("신규 등록 요청인 경우") {
            val domain = v1AccountFixture.make(
                acquirerId = acquirerId,
                acquirerType = acquirerType,
                acquirerName = acquirerName
            )
            val result = v1AccountManagementService.save(domain)

            then("저장 성공 결과 정상 확인한다") {
                result shouldNotBe null
                result.id shouldNotBe null
                result.acquirer.id shouldBe acquirerId
                result.acquirer.type shouldBe acquirerType
                result.acquirer.name shouldBe acquirerName
            }
        }
    }

    given("외화 계좌 정보 단건 조회 요청하여") {
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

    given("외화 계좌 정보 다건 조회 요청하여") {
        val pageable = PageableRequest()
        var predicate = V1AccountPredicate(acquirer = V1AcquirerPredicate(name = "DUMMY"))

        `when`("요청 'acquirerName' 조건 일치한 정보 없는 경우") {
            val result = v1AccountManagementService.findAllByPredicate(predicate, pageable)

            then("조회 결과 '0'건 정상 확인한다") {
                result.pageable.numberOfElements shouldBe 0
                result.pageable.totalElements shouldBe 0
            }
        }

        // 외화 계좌 정보 DB 저장
        val acquirerName = "DUMMY"
        val saved2 = v1AccountManagementService.save(v1AccountFixture.make(acquirerName = acquirerName))

        predicate = V1AccountPredicate(acquirer = V1AcquirerPredicate(id = saved2.acquirer.id))

        `when`("요청 'acquirerId' 조건 일치한 정보 '1'건 있는 경우") {
            val result = v1AccountManagementService.findAllByPredicate(predicate, pageable)

            then("조회 결과 '1'건 정상 확인한다") {
                result.pageable.numberOfElements shouldBe 1
                result.pageable.totalElements shouldBe 1
            }
        }

        // 외화 계좌 정보 DB 저장
        v1AccountManagementService.save(v1AccountFixture.make(acquirerName = acquirerName))

        predicate = V1AccountPredicate(acquirer = V1AcquirerPredicate(name = acquirerName))

        `when`("요청 'acquirerName' 조건 일치한 정보 '2'건 있는 경우") {
            val result = v1AccountManagementService.findAllByPredicate(predicate, pageable)

            then("조회 결과 '2'건 정상 확인한다") {
                result.pageable.numberOfElements shouldBe 2
                result.pageable.totalElements shouldBe 2
            }
        }
    }

    given("외화 계좌 정보 변경 요청하여") {
        val newEntity = v1AccountManagementService.save(v1AccountFixture.make())

        `when`("요청 'id' 기준 동일한 외화 계좌 정보 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1AccountManagementService.update(V1AccountPredicate(id = generateSequence())) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        val accountId = newEntity.id!!

        `when`("동일한 'acquirer' 정보 이미 등록되어 있는 경우") {
            val predicate = V1AccountPredicate(id = accountId, acquirer = V1AcquirerPredicate(saved.acquirer))
            val exception = shouldThrow<InternalServiceException> { v1AccountManagementService.update(predicate) }

            then("'ACCOUNT_ACQUIRER_IS_DUPLICATED' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_ACQUIRER_IS_DUPLICATED
            }
        }

        `when`("신규 'acquirer.id' 정보 변경인 경우") {
            val newAcquirer = V1AcquirerPredicate(generateUUID(), FX_DEPOSIT, "외화 예치금 계좌")
            val predicate = V1AccountPredicate(id = accountId, acquirer = newAcquirer)
            val result = v1AccountManagementService.update(predicate)

            then("변경된 'acquirerId' 정보 정상 확인한다") {
                result.id shouldBe newEntity.id
                result.acquirer.id shouldBe newAcquirer.id
                result.acquirer.type shouldBe newAcquirer.type
                result.acquirer.name shouldBe newAcquirer.name
            }
        }

        `when`("'currency' 정보 변경인 경우") {
            val newCurrency = "KRW"
            val predicate = V1AccountPredicate(id = accountId, currency = newCurrency)
            val result = v1AccountManagementService.update(predicate)

            then("변경된 'currency' 정보 정상 확인한다") {
                result.id shouldBe newEntity.id
                result.currency shouldBe newCurrency
            }
        }

        `when`("'balance' 정보 변경인 경우") {
            val newBalance = BigDecimal(10000000)
            val predicate = V1AccountPredicate(id = accountId, balance = newBalance)
            val result = v1AccountManagementService.update(predicate)

            then("변경된 'balance' 정보 정상 확인한다") {
                result.id shouldBe newEntity.id
                result.balance shouldBe newBalance
            }
        }

        `when`("'status' 정보 'INACTIVE' 변경인 경우") {
            val predicate = V1AccountPredicate(id = accountId, status = INACTIVE)
            val result = v1AccountManagementService.update(predicate)

            then("변경된 'status' 정보 정상 확인한다") {
                result.id shouldBe newEntity.id
                result.status shouldBe INACTIVE
            }
        }

        `when`("'status' 정보 'DELETED' 변경인 경우") {
            val predicate = V1AccountPredicate(id = accountId, status = DELETED)
            val result = v1AccountManagementService.update(predicate)

            then("변경된 'status' 정보 정상 확인한다") {
                result.id shouldBe newEntity.id
                result.status shouldBe DELETED
            }
        }

    }

    given("외화 계좌 정보 'status' 변경 요청하여") {
        val entity = v1AccountManagementService.save(v1AccountFixture.make())
        val accountId = entity.id!!
        // `DELETED` 상태 변경 사전 처리
        v1AccountManagementService.update(V1AccountPredicate(id = accountId, status = DELETED))

        `when`("이미 'DELETED' 상태 변경된 외화 계좌 정보 변경 요청인 경우") {
            val predicate = V1AccountPredicate(id = accountId, status = ACTIVE)
            val exception = shouldThrow<InternalServiceException> { v1AccountManagementService.update(predicate) }

            then("'ACCOUNT_ACQUIRER_IS_DUPLICATED' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_STATUS_IS_DELETED
            }
        }

    }

})