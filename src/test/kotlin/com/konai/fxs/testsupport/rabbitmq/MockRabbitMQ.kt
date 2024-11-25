package com.konai.fxs.testsupport.rabbitmq

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory
import com.konai.fxs.common.EMPTY
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import kotlin.reflect.KFunction2

object MockRabbitMQ {

    val connectionFactory: ConnectionFactory by lazy {
        MockConnectionFactoryFactory
            .build()
            .enableConsistentHashPlugin()
            .let(::CachingConnectionFactory)
    }
    val rabbitTemplate: RabbitTemplate by lazy {
        RabbitTemplate(connectionFactory).apply { messageConverter = Jackson2JsonMessageConverter() }
    }
    private val rabbitAdmin: RabbitAdmin by lazy { RabbitAdmin(connectionFactory) }

    fun binding(exchange: Exchange? = null) {
        if (exchange == null) {
            Exchange.entries.forEach { it.binding(rabbitAdmin) }
        } else {
            exchange.binding(rabbitAdmin)
        }
    }

    enum class Exchange(
        val exchangeName: String,
        val queueName: String,
        val routingKey: String,
        private val bindingFunction: KFunction2<Exchange, RabbitAdmin, Unit>
    ) {

        TEST_DIRECT_EXCHANGE(
            exchangeName = "fxs.test.direct.exchange",
            queueName = "fxs.test.direct.queue",
            routingKey = "fxs.test.direct.routing.key",
            Exchange::setupDirectExchange
        ),
        TEST_TOPIC_EXCHANGE(
            exchangeName = "fxs.test.topic.exchange",
            queueName = "fxs.test.topic.queue",
            routingKey = "fxs.test.topic.routing.key",
            Exchange::setupTopicExchange
        ),
        TEST_FANOUT_EXCHANGE(
            exchangeName = "fxs.test.fanout.exchange",
            queueName = "fxs.test.fanout.queue",
            routingKey = EMPTY,
            Exchange::setupFanoutExchange
        ),
        V1_SAVE_TRANSACTION_EXCHANGE(
            exchangeName = "fxs.v1.save.transaction.exchange",
            queueName = "fxs.v1.save.transaction.queue",
            routingKey = "fxs.v1.save.transaction.routingKey",
            Exchange::setupDirectExchange
        )
        ;

        fun binding(rabbitAdmin: RabbitAdmin) {
            bindingFunction.invoke(this, rabbitAdmin)
        }

        private fun setupDirectExchange(rabbitAdmin: RabbitAdmin) {
            val exchange = DirectExchange(exchangeName)
            val queue = Queue(queueName, false)
            rabbitAdmin.declareExchange(exchange)
            rabbitAdmin.declareQueue(queue)
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey))
        }

        private fun setupTopicExchange(rabbitAdmin: RabbitAdmin) {
            val exchange = TopicExchange(exchangeName)
            val queue = Queue(queueName, false)
            rabbitAdmin.declareExchange(exchange)
            rabbitAdmin.declareQueue(queue)
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey))
        }

        private fun setupFanoutExchange(rabbitAdmin: RabbitAdmin) {
            val exchange = FanoutExchange(exchangeName)
            val queue = Queue(queueName, false)
            rabbitAdmin.declareExchange(exchange)
            rabbitAdmin.declareQueue(queue)
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange))
        }

    }

}