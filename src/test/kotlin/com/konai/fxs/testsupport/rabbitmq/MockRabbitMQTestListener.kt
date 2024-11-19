package com.konai.fxs.testsupport.rabbitmq

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

class MockRabbitMQTestListener(
    private val mockRabbitMQExchange: MockRabbitMQExchange
) : TestListener {

    override suspend fun beforeSpec(spec: Spec) {
        MockRabbitMQ.binding(mockRabbitMQExchange)
    }

}