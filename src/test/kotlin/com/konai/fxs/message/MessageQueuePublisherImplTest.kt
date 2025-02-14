package com.konai.fxs.message

import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_SAVE_TRANSACTION_EXCHANGE
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQTestListener
import com.konasl.commonlib.springweb.correlation.headerpropagator.CorrelationHeaderField
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MessageQueuePublisherImplTest : CustomStringSpec({

    listeners(MockRabbitMQTestListener(V1_SAVE_TRANSACTION_EXCHANGE))

    val rabbitTemplate = dependencies.rabbitTemplate
    val messageQueuePublisher = dependencies.messageQueuePublisher

    val v1TransactionFixture = dependencies.v1TransactionFixture

    "외화 계좌 거래 내역 저장 메시지 발행 정상 확인한다" {
        // given
        val transaction = v1TransactionFixture.make()
        val message = V1SaveTransactionMessage(transaction)
        val exchange = MessageQueueExchange.V1_SAVE_TRANSACTION_EXCHANGE

        // when
        messageQueuePublisher.sendDirectMessage(exchange, message)

        // then
        val received = rabbitTemplate.receive(MessageQueue.V1_SAVE_TRANSACTION_QUEUE)!!
        received.messageProperties.headers[CorrelationHeaderField.CORRELATION_ID_HEADER_FIELD.getName()] shouldNotBe null

        val result = rabbitTemplate.messageConverter.fromMessage(received) as V1SaveTransactionMessage
        result shouldNotBe null
        result.transaction.id shouldBe transaction.id
        result.transaction.baseAcquirer.id shouldBe transaction.baseAcquirer.id
    }

})