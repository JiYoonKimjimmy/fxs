package com.konai.fxs.testsupport.rabbitmq

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

class MockRabbitMQTestListener(
    private vararg val exchanges: MockRabbitMQ.Exchange
) : TestListener {

    override suspend fun beforeSpec(spec: Spec) {
        exchanges.forEach(MockRabbitMQ::binding)
    }

}