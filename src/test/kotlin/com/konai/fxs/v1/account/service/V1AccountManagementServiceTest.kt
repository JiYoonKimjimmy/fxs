package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.repository.FakeV1AccountRepositoryImpl
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.*

class V1AccountManagementServiceTest : BehaviorSpec({

    val fakeV1AccountRepositoryImpl = FakeV1AccountRepositoryImpl(V1AccountMapper())
    val v1AccountManagementService = V1AccountManagementService(fakeV1AccountRepositoryImpl)

    lateinit var saved: V1Account

    given("외화 계좌 정보 저장 요청되어") {
        val accountNumber = UUID.randomUUID().toString()
        val domain = V1Account(accountNumber = accountNumber)

        `when`("신규 등록 요청인 경우") {
            val result = v1AccountManagementService.save(domain)

            then("저장 결과 성공 정상 확인한다") {
                result shouldNotBe null
                result.id shouldNotBe null
                result.accountNumber shouldBe accountNumber
            }

            saved = result
        }
    }

    given("외화 계좌 정보 by id 조회 요청하여") {
        val id = saved.id!!

        `when`("정보 있는 경우") {
            val result = v1AccountManagementService.findOne(id)

            then("조회 결과 성공 정상 확인한다") {
                result shouldNotBe null
                result.id shouldBe id
                result.accountNumber shouldBe saved.accountNumber
            }
        }
    }

})