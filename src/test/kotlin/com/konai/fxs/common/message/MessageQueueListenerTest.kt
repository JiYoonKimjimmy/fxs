package com.konai.fxs.common.message

import com.konai.fxs.common.enumerate.TransactionStatus.EXPIRED
import com.konai.fxs.common.enumerate.TransactionStatus.PREPARED
import com.konai.fxs.common.message.MessageQueueExchange.V1_EXPIRE_PREPARED_TRANSACTION_EXCHANGE
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

    lateinit var preparedTransaction: V1Transaction

    beforeSpec {
        // 외화 계좌 출금 준비 거래 DB 정보 저장
        preparedTransaction = v1TransactionRepository.save(v1TransactionFixture.make(status = PREPARED))
    }

    given("외화 계좌 거래 내역 저장 Message 수신하여") {
        val exchange = V1_SAVE_TRANSACTION_EXCHANGE.exchangeName
        val routingKey = V1_SAVE_TRANSACTION_EXCHANGE.routingKey
        val transaction = v1TransactionFixture.make()
        val message = V1SaveTransactionMessage(transaction)

        `when`("성공인 경우") {
            defaultRabbitTemplate.convertAndSend(exchange, routingKey, message)

            then("외화 계좌 거래 내역 저장 처리 결과 정상 확인한다") {
                val entity = v1TransactionRepository.findByPredicate(V1TransactionPredicate(id = transaction.id))
                entity!! shouldNotBe null
            }
        }
    }

    given("외화 계좌 출금 거래 만료 Message 수신하여") {
        val exchange = V1_EXPIRE_PREPARED_TRANSACTION_EXCHANGE.exchangeName
        val routingKey = V1_EXPIRE_PREPARED_TRANSACTION_EXCHANGE.routingKey
        val message = V1ExpirePreparedTransactionMessage(transactionId = preparedTransaction.id!!, amount = preparedTransaction.amount.toLong())

        // 출금 준기 거래 금액 합계 Cache 정보 생성
        v1TransactionCacheService.incrementPreparedWithdrawalTotalAmountCache(preparedTransaction.acquirer, preparedTransaction.amount)

        `when`("성공인 경우") {
            defaultRabbitTemplate.convertAndSend(exchange, routingKey, message)

            val config = eventuallyConfig {
                initialDelay = 1.seconds
                duration = 5.seconds
            }
            eventually(config) {
                then("외화 계좌 출금 거래 내역 만료 처리 결과 정상 확인한다") {
                    val result = v1TransactionRepository.findByPredicate(V1TransactionPredicate(id = preparedTransaction.id))
                    result!! shouldNotBe null
                    result.status shouldBe EXPIRED
                }

                then("외화 계좌 출금 거래 금액 합계 감액 처리 결과 정상 확인한다") {
                    val result = v1TransactionCacheService.findPreparedWithdrawalTotalAmountCache(preparedTransaction.acquirer)
                    result.toLong() shouldBe 0
                }
            }
        }
    }

})