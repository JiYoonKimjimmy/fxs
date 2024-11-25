package com.konai.fxs.common.message

import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_SAVE_TRANSACTION_EXCHANGE
import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.matchers.shouldNotBe
import org.springframework.amqp.rabbit.core.RabbitTemplate

@CustomSpringBootTest
class MessageQueueListenerTest(
    private val defaultRabbitTemplate: RabbitTemplate,
    private val v1TransactionRepository: V1TransactionRepository
) : CustomBehaviorSpec({

    val v1TransactionFixture = dependencies.v1TransactionFixture

    given("외화 계좌 거래 내역 저장 Message 수신하여") {
        val exchange = V1_SAVE_TRANSACTION_EXCHANGE.exchangeName
        val routingKey = V1_SAVE_TRANSACTION_EXCHANGE.routingKey
        val transaction = v1TransactionFixture.make()
        val message = V1SaveTransactionMessage(transaction)

        `when`("성공인 경우") {
            defaultRabbitTemplate.convertAndSend(exchange, routingKey, message)

            then("외화 계좌 거래 내역 저장 처리 결과 정상 확인한다") {
                val entity = v1TransactionRepository.findByPredicate(V1TransactionPredicate(id = transaction.id))
                entity shouldNotBe null
            }
        }
    }

})