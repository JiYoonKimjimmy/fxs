package com.konai.fxs.common.message

import com.konai.fxs.common.enumerate.TransactionStatus.EXPIRED
import com.konai.fxs.common.enumerate.TransactionStatus.PENDING
import com.konai.fxs.common.message.MessageQueueExchange.V1_EXCHANGE_RATE_TIMER_EXCHANGE
import com.konai.fxs.common.message.MessageQueueExchange.V1_EXPIRE_TRANSACTION_EXCHANGE
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_SAVE_TRANSACTION_EXCHANGE
import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.service.cache.V1TransactionCacheService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.amqp.rabbit.core.RabbitTemplate
import kotlin.time.Duration.Companion.seconds

@CustomSpringBootTest
class MessageQueueListenerTest(
    private val defaultRabbitTemplate: RabbitTemplate,
    private val v1TransactionRepository: V1TransactionRepository,
    private val v1TransactionCacheService: V1TransactionCacheService,
) : CustomBehaviorSpec({

    val v1TransactionFixture = dependencies.v1TransactionFixture

    val eventuallyConfig = eventuallyConfig {
        initialDelay = 1.seconds
        duration = 5.seconds
    }

    lateinit var transaction: V1Transaction

    beforeSpec {
        // 외화 계좌 출금 대기 거래 DB 정보 저장
        transaction = v1TransactionRepository.save(v1TransactionFixture.make(status = PENDING))
    }

    given("외화 계좌 거래 내역 저장 Message 수신하여") {
        val exchange = V1_SAVE_TRANSACTION_EXCHANGE.exchangeName
        val routingKey = V1_SAVE_TRANSACTION_EXCHANGE.routingKey
        val message = V1SaveTransactionMessage(v1TransactionFixture.make())

        `when`("성공인 경우") {
            defaultRabbitTemplate.convertAndSend(exchange, routingKey, message)

            then("외화 계좌 거래 내역 저장 처리 결과 정상 확인한다") {
                val entity = v1TransactionRepository.findByPredicate(V1TransactionPredicate(id = message.transaction.id))
                entity!! shouldNotBe null
            }
        }
    }

    given("외화 계좌 출금 거래 만료 Message 수신하여") {
        val exchange = V1_EXPIRE_TRANSACTION_EXCHANGE.exchangeName
        val routingKey = V1_EXPIRE_TRANSACTION_EXCHANGE.routingKey
        val message = V1ExpireTransactionMessage(transactionId = transaction.id!!, amount = transaction.amount.toLong())

        // 출금 거래 대기 금액 Cache 정보 생성
        v1TransactionCacheService.incrementWithdrawalTransactionPendingAmountCache(transaction.baseAcquirer, transaction.amount)

        `when`("성공인 경우") {
            defaultRabbitTemplate.convertAndSend(exchange, routingKey, message)

            eventually(eventuallyConfig) {
                then("외화 계좌 출금 거래 내역 만료 처리 결과 정상 확인한다") {
                    val result = v1TransactionRepository.findByPredicate(V1TransactionPredicate(id = transaction.id))
                    result!! shouldNotBe null
                    result.status shouldBe EXPIRED
                }

                then("외화 계좌 출금 거래 대기 금액 감액 처리 결과 정상 확인한다") {
                    val result = v1TransactionCacheService.findWithdrawalTransactionPendingAmountCache(transaction.baseAcquirer)
                    result.toLong() shouldBe 0
                }
            }
        }
    }
    
    given("환율 정보 수집 Timer Message 수신하여") {
        val exchange = V1_EXCHANGE_RATE_TIMER_EXCHANGE.exchangeName
        val routingKey = V1_EXCHANGE_RATE_TIMER_EXCHANGE.routingKey
        val message = V1CollectExchangeRateTimerMessage(index = 1, date = "20241217")
        
        `when`("성공인 경우") {
            defaultRabbitTemplate.convertAndSend(exchange, routingKey, message)

            eventually(eventuallyConfig) {
                then("최근 환율 정보 Cache 변경 처리 결과 정상 확인한다") {

                }

                then("Koreaexim 환율 정보 DB 저장 결과 정상 확인한다") {

                }
            }
        }
    }

})