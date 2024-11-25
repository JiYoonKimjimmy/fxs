package com.konai.fxs.testsupport.rabbitmq

import com.konai.fxs.common.EMPTY
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.*
import io.kotest.matchers.shouldBe

class MockRabbitMQTest : CustomStringSpec({

    listeners(MockRabbitMQTestListener(
        TEST_DIRECT_EXCHANGE,
        TEST_TOPIC_EXCHANGE,
        TEST_FANOUT_EXCHANGE
    ))

    val rabbitTemplate = MockRabbitMQ.rabbitTemplate

    "'Direct' exchange 메시지 발행/수신 정상 확인한다" {
        // given
        val exchangeName = TEST_DIRECT_EXCHANGE.exchangeName
        val queueName = TEST_DIRECT_EXCHANGE.queueName
        val routingKey = TEST_DIRECT_EXCHANGE.routingKey
        val message = "Hello, 'Direct' World!"

        // when
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message)

        // then
        rabbitTemplate.receiveAndConvert(queueName) shouldBe "Hello, 'Direct' World!"
    }

    "'Topic' exchange 메시지 발행/수신 정상 확인한다" {
        // given
        val exchangeName = TEST_TOPIC_EXCHANGE.exchangeName
        val queueName = TEST_TOPIC_EXCHANGE.queueName
        val routingKey = TEST_TOPIC_EXCHANGE.routingKey
        val message = "Hello, 'Topic' World!"

        // when
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message)

        // then
        rabbitTemplate.receiveAndConvert(queueName) shouldBe "Hello, 'Topic' World!"
    }

    "'Fanout' exchange 메시지 발행/수신 정상 확인한다" {
        // given
        val exchangeName = TEST_FANOUT_EXCHANGE.exchangeName
        val queueName = TEST_FANOUT_EXCHANGE.queueName
        val message = "Hello, 'Fanout' World!"

        // when
        rabbitTemplate.convertAndSend(exchangeName, EMPTY, message)

        // then
        rabbitTemplate.receiveAndConvert(queueName) shouldBe "Hello, 'Fanout' World!"
    }

})