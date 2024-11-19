package com.konai.fxs.testsupport.rabbitmq

import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQExchange.*
import io.kotest.matchers.shouldBe

class MockRabbitMQTest : CustomStringSpec({

    listeners(MockRabbitMQTestListener(TEST_TOPIC_EXCHANGE))

    val exchangeName = TEST_TOPIC_EXCHANGE.exchangeName
    val queueName = TEST_TOPIC_EXCHANGE.queueName
    val routingKey = TEST_TOPIC_EXCHANGE.routingKey
    val rabbitTemplate = MockRabbitMQ.rabbitTemplate

    "RabbitMQ Mock 활용한 메시지 Pub/Sub 테스트 정상 확인한다" {
        // given
        val message = "Hello World!"

        // when
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message)
        val result = rabbitTemplate.receiveAndConvert(queueName)

        // then
        result shouldBe "Hello World!"
    }

})