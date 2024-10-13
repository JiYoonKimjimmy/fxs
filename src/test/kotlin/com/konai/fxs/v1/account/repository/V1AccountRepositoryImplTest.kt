package com.konai.fxs.v1.account.repository

import com.konai.fxs.infra.config.JpaAuditorConfig
import com.konai.fxs.v1.account.repository.entity.V1AccountEntityFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Import(JpaAuditorConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
class V1AccountRepositoryImplTest(
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : BehaviorSpec({

    val fixture = V1AccountEntityFixture()

    given("외화 계좌 Entity 정보 등록 요청되어") {
        val entity = fixture.make()

        `when`("신규 정보인 경우") {
            val result = v1AccountJpaRepository.save(entity)

            then("저장 결과 성공 정상 확인한다") {
                result shouldNotBe null
                result.id shouldBe entity.id
                result.accountNumber shouldBe entity.accountNumber
            }
        }
    }

})