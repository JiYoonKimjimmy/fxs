package com.konai.fxs.v1.account.controller

import com.konai.fxs.common.enumerate.AccountStatus.ACTIVE
import com.konai.fxs.common.enumerate.AccountStatus.DELETED
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.ResultStatus
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.CustomSpringBootTest
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.controller.model.V1CreateAccountRequest
import com.konai.fxs.v1.account.controller.model.V1FindOneAccountRequest
import com.konai.fxs.v1.account.repository.V1AccountJpaRepository
import io.kotest.matchers.shouldBe
import org.hamcrest.Matchers.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@CustomSpringBootTest
class V1AccountManagementControllerTest(
    private val mockMvc: MockMvc,
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : CustomBehaviorSpec({

    val objectMapper = dependencies.objectMapper
    val v1AccountEntityFixture = dependencies.v1AccountEntityFixture
    val v1FindAllAccountRequestFixture = dependencies.v1FindAllAccountRequestFixture
    val v1UpdateAccountRequestFixture = dependencies.v1UpdateAccountRequestFixture

    given("외화 계좌 등록 API 요청하여") {
        val createAccountUrl = "/api/v1/account"
        val acquirerId = generateUUID()
        val acquirerType = FX_DEPOSIT
        val acquirerName = "외화 예치금 계좌"
        val currency = "USD"
        val minRequiredBalance = 100000L
        val request = V1CreateAccountRequest(
            acquirerId = acquirerId,
            acquirerType = acquirerType,
            acquirerName = acquirerName,
            currency = currency,
            minRequiredBalance = minRequiredBalance,
        )

        `when`("신규 외화 계좌 정보인 경우") {
            val result = mockMvc
                .post(createAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'201 Created' 성공 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isCreated() }
                        content {
                            jsonPath("data.accountId", greaterThan(0))
                            jsonPath("data.acquirerId", equalTo(acquirerId))
                            jsonPath("data.acquirerType", equalTo(acquirerType.name))
                            jsonPath("data.acquirerName", equalTo(acquirerName))
                            jsonPath("data.currency", equalTo(currency))
                            jsonPath("data.minRequiredBalance", equalTo(minRequiredBalance.toInt()))
                            jsonPath("data.averageExchangeRate", equalTo(0.toDouble()))
                        }
                    }
            }
        }
    }

    given("외화 계좌 단건 API 요청하여") {
        val findOneAccountUrl = "/api/v1/account/one"

        `when`("'accountId' 기준 일치한 외화 계좌 정보 없는 경우") {
            val request = V1FindOneAccountRequest(accountId = generateSequence(), acquirerId = null, acquirerType = null)
            val result = mockMvc
                .post(findOneAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'404 Not Found - 210_1001_001' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isNotFound() }
                        content {
                            jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                            jsonPath("result.code", equalTo("210_1001_001"))
                            jsonPath("result.message", equalTo("Account management service failed. Account not found."))
                        }
                    }
            }
        }

        // 외화 계좌 정보 DB 저장
        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())

        `when`("'accountId' 기준 일치한 외화 계좌 정보 있는 경우") {
            val request = V1FindOneAccountRequest(accountId = entity.id, acquirerId = null, acquirerType = null)
            val result = mockMvc
                .post(findOneAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("조회 결과 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("data.accountId", equalTo(entity.id?.toInt()))
                            jsonPath("data.acquirerId", equalTo(entity.acquirer.id))
                            jsonPath("data.acquirerType", equalTo(entity.acquirer.type.name))
                            jsonPath("data.acquirerName", equalTo(entity.acquirer.name))
                            jsonPath("data.currency", equalTo(entity.currency))
                            jsonPath("data.minRequiredBalance", equalTo(entity.minRequiredBalance.toInt()))
                            jsonPath("data.averageExchangeRate", equalTo(entity.averageExchangeRate.toDouble()))
                        }
                    }
            }
        }
    }

    given("외화 계좌 다건 API 요청하여") {
        val findAllAccountUrl = "/api/v1/account/all"

        `when`("저장된 외화 계좌 정보 없는 경우") {
            val request = v1FindAllAccountRequestFixture.make()
            val result = mockMvc
                .post(findAllAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("조회 결과 '0'건 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("pageable.totalElements", equalTo(0))
                            jsonPath("content", hasSize<Any>(0))
                        }
                    }
            }
        }

        // 외화 계좌 정보 DB 저장
        val entity1 = v1AccountJpaRepository.save(v1AccountEntityFixture.make())
        val entity2 = v1AccountJpaRepository.save(v1AccountEntityFixture.make())

        `when`("요청 조건 없이 전체 조회 요청인 경우") {
            val request = v1FindAllAccountRequestFixture.make()
            val result = mockMvc
                .post(findAllAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("조회 결과 '2'건 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("pageable.totalElements", equalTo(2))
                            jsonPath("content", hasSize<Any>(2))
                        }
                    }
            }
        }

        `when`("'accountId' 조건 조회 요청인 경우") {
            val request = v1FindAllAccountRequestFixture.make(accountId = entity1.id)
            val result = mockMvc
                .post(findAllAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("조회 결과 '1'건 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("pageable.totalElements", equalTo(1))
                            jsonPath("content", hasSize<Any>(1))
                        }
                    }
            }
        }

        `when`("'acquirerId' 조건 조회 요청인 경우") {
            val request = v1FindAllAccountRequestFixture.make(acquirerId = entity2.acquirer.id)
            val result = mockMvc
                .post(findAllAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("조회 결과 '1'건 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("pageable.totalElements", equalTo(1))
                            jsonPath("content", hasSize<Any>(1))
                        }
                    }
            }
        }

        `when`("'acquirerName' 조건 조회 요청인 경우") {
            val request = v1FindAllAccountRequestFixture.make(acquirerName = entity2.acquirer.name)
            val result = mockMvc
                .post(findAllAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("조회 결과 '2'건 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("pageable.totalElements", equalTo(2))
                            jsonPath("content", hasSize<Any>(2))
                        }
                    }
            }
        }

    }

    given("외화 계좌 변경 API 요청하여") {
        val updateAccountUrl = "/api/v1/account/update"

        `when`("'acquirer' 요청 필수 여부 검증 실패하는 경우") {
            val request = v1UpdateAccountRequestFixture.make(acquirerId = generateUUID())
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'400 Bad Request - 210_1001_905' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isBadRequest() }
                        content {
                            jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                            jsonPath("result.code", equalTo("210_1001_905"))
                            jsonPath("result.message", equalTo("Account management service failed. Argument not valid."))
                        }
                    }
            }
        }

        `when`("'accountId' 기준 일치한 외화 계좌 정보 없는 경우") {
            val request = v1UpdateAccountRequestFixture.make()
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'404 Not Found - 210_1001_001' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isNotFound() }
                        content {
                            jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                            jsonPath("result.code", equalTo("210_1001_001"))
                            jsonPath("result.message", equalTo("Account management service failed. Account not found."))
                        }
                    }
            }
        }

        // 외화 계좌 정보 DB 저장
        val entity = v1AccountJpaRepository.save(v1AccountEntityFixture.make())

        `when`("'accountId' 기준 일치한 외화 계좌 정보 있는 경우") {
            val acquirerId = generateUUID()
            val request = v1UpdateAccountRequestFixture.make(
                accountId = entity.id!!,
                acquirerId = acquirerId,
                acquirerType = entity.acquirer.type,
                acquirerName = entity.acquirer.name
            )
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("변경 결과 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("data.accountId", equalTo(entity.id?.toInt()))
                            jsonPath("data.acquirerId", equalTo(acquirerId))
                            jsonPath("data.acquirerType", equalTo(entity.acquirer.type.name))
                            jsonPath("data.acquirerName", equalTo(entity.acquirer.name))
                            jsonPath("data.currency", equalTo(entity.currency))
                            jsonPath("data.minRequiredBalance", equalTo(entity.minRequiredBalance.toInt()))
                            jsonPath("data.averageExchangeRate", equalTo(entity.averageExchangeRate.toDouble()))
                        }
                    }
            }
        }

        `when`("'accountId' 기준 일치한 외화 계좌 정보 'status' 상태 변경인 경우") {
            val request = v1UpdateAccountRequestFixture.make(accountId = entity.id!!, status = DELETED)
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("변경 결과 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isOk() }
                        content {
                            jsonPath("data.accountId", equalTo(entity.id?.toInt()))
                            jsonPath("data.acquirerId", equalTo(entity.acquirer.id))
                            jsonPath("data.acquirerType", equalTo(entity.acquirer.type.name))
                            jsonPath("data.acquirerName", equalTo(entity.acquirer.name))
                            jsonPath("data.currency", equalTo(entity.currency))
                            jsonPath("data.minRequiredBalance", equalTo(entity.minRequiredBalance.toInt()))
                            jsonPath("data.averageExchangeRate", equalTo(entity.averageExchangeRate.toDouble()))
                            jsonPath("data.status", equalTo(DELETED.name))
                        }
                    }
            }

            then("외화 계좌 정보 상태 'DELETED' 변경 정상 확인한다") {
                val deleted = v1AccountJpaRepository.findById(entity.id!!).orElseThrow()
                deleted.status shouldBe DELETED
            }
        }

        `when`("이미 'DELETED' 상태 변경 된 'accountId' 기준 일치한 외화 계좌 정보 변경인 경우") {
            // 외화 계좌 정보 DB 저장
            val newEntity = v1AccountJpaRepository.save(v1AccountEntityFixture.make(status = DELETED))
            val request = v1UpdateAccountRequestFixture.make(accountId = newEntity.id!!, status = ACTIVE)
            val result = mockMvc
                .post(updateAccountUrl) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }
                .andDo { print() }

            then("'400 Bad Request - 210_1001_003' 에러 응답 정상 확인한다") {
                result
                    .andExpect {
                        status { isBadRequest() }
                        content {
                            jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                            jsonPath("result.code", equalTo("210_1001_003"))
                            jsonPath("result.message", equalTo("Account management service failed. Account's status is already deleted."))
                        }
                    }
            }
        }
    }

})