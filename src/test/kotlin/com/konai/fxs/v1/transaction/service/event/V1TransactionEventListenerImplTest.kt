package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.common.enumerate.TransactionStatus.PENDING
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents

@RecordApplicationEvents
@CustomSpringBootTest
class V1TransactionEventListenerImplTest : CustomBehaviorSpec() {

    @Autowired lateinit var v1TransactionRepository: V1TransactionRepository
    @Autowired lateinit var applicationEventPublisher: ApplicationEventPublisher
    @Autowired lateinit var applicationEvents: ApplicationEvents

    private val v1TransactionFixture = dependencies.v1TransactionFixture
    private val v1TransactionMapper = dependencies.v1TransactionMapper

    init {
        this.given("외화 계좌 거래 내역 'COMPLETED' 정보 저장 Event 발행 요청하여") {
            val transaction = v1TransactionFixture.make(status = COMPLETED)
            val event = v1TransactionMapper.domainToSaveTransactionEvent(transaction)

            `when`("거래 내역 정보 저장 Event 정상 발행된 경우") {
                applicationEventPublisher.publishEvent(event)

                then("저장 결과 정상 확인한다") {
                    val predicate = V1TransactionPredicate(acquirer = V1AcquirerPredicate(id = transaction.acquirer.id, type = transaction.acquirer.type))
                    val entity = v1TransactionRepository.findByPredicate(predicate)
                    entity!! shouldNotBe null
                    entity.id!! shouldBeGreaterThan 0L
                    entity.status shouldBe COMPLETED
                }

                then("Event 발행 '1회' 정상 확인한다") {
                    applicationEvents.stream(V1SaveTransactionEvent::class.java).count() shouldBe 1
                }
            }
        }

        this.given("외화 계좌 출금 거래 만료 Event 발행 요청하여") {
            val transaction = v1TransactionFixture.make(status = PENDING)
            val event = v1TransactionMapper.domainToExpireTransactionEvent(transaction)

            `when`("출금 거래 만료 Event 정상 발행된 경우") {
                applicationEventPublisher.publishEvent(event)

                then("Event 발행 '1회' 정상 확인한다") {
                    applicationEvents.stream(V1ExpireTransactionEvent::class.java).count() shouldBe 1
                }
            }
        }

        this.given("외화 계좌 거래 내역 'PREPARED' 정보 저장 Event 발행 요청하여") {
            val transaction = v1TransactionFixture.make(status = PENDING)
            val event = v1TransactionMapper.domainToSaveTransactionEvent(transaction)

            `when`("거래 내역 정보 저장 Event 정상 발행된 경우") {
                applicationEventPublisher.publishEvent(event)

                then("저장 결과 정상 확인한다") {
                    val predicate = V1TransactionPredicate(acquirer = V1AcquirerPredicate(id = transaction.acquirer.id, type = transaction.acquirer.type))
                    val entity = v1TransactionRepository.findByPredicate(predicate)
                    entity!! shouldNotBe null
                    entity.id!! shouldBeGreaterThan 0L
                    entity.status shouldBe PENDING
                }

                then("거래 내역 정보 저장 Event 발행 '1회' 정상 확인한다") {
                    applicationEvents.stream(V1SaveTransactionEvent::class.java).count() shouldBe 1
                }

                then("출금 거래 만료 Event 발행 '1회' 정상 확인한다") {
                    applicationEvents.stream(V1ExpireTransactionEvent::class.java).count() shouldBe 1
                }
            }
        }

    }

}