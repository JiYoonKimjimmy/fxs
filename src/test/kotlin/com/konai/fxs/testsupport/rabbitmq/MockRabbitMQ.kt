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
        private val bindingFunction: KFunction2<Exchange, RabbitAdmin, Unit>,
        private val dlx: Exchange? = null
    ) {

        TEST_DIRECT_EXCHANGE(
            exchangeName = "fxs.test.direct.exchange",
            queueName = "fxs.test.direct.queue",
            routingKey = "fxs.test.direct.routing.key",
            bindingFunction = Exchange::setupDirectExchange
        ),
        TEST_TOPIC_EXCHANGE(
            exchangeName = "fxs.test.topic.exchange",
            queueName = "fxs.test.topic.queue",
            routingKey = "fxs.test.topic.routing.key",
            bindingFunction = Exchange::setupTopicExchange
        ),
        TEST_FANOUT_EXCHANGE(
            exchangeName = "fxs.test.fanout.exchange",
            queueName = "fxs.test.fanout.queue",
            routingKey = EMPTY,
            bindingFunction = Exchange::setupFanoutExchange
        ),
        V1_SAVE_TRANSACTION_EXCHANGE(
            exchangeName = "fxs.v1.save.transaction.exchange",
            queueName = "fxs.v1.save.transaction.queue",
            routingKey = "fxs.v1.save.transaction.routing-key",
            bindingFunction = Exchange::setupDirectExchange
        ),
        V1_EXPIRE_TRANSACTION_DL_EXCHANGE(
            exchangeName = "fxs.v1.expire.transaction.dl.exchange",
            queueName = "fxs.v1.expire.transaction.dl.queue",
            routingKey = "fxs.v1.expire.transaction.dl.routing-key",
            Exchange::setupDirectExchange
        ),
        V1_EXPIRE_TRANSACTION_EXCHANGE(
            exchangeName = "fxs.v1.expire.transaction.exchange",
            queueName = "fxs.v1.expire.transaction.queue",
            routingKey = "fxs.v1.expire.transaction.routing-key",
            bindingFunction = Exchange::setupDeadLetterExchange,
            dlx = V1_EXPIRE_TRANSACTION_DL_EXCHANGE
        ),
        V1_EXCHANGE_RATE_TIMER_DL_EXCHANGE(
            exchangeName = "fxs.v1.exchange.rate.timer.dl.exchange",
            queueName = "fxs.v1.exchange.rate.timer.dl.queue",
            routingKey = "fxs.v1.exchange.rate.timer.dl.routing-key",
            Exchange::setupDirectExchange
        ),
        V1_EXCHANGE_RATE_TIMER_EXCHANGE(
            exchangeName = "fxs.v1.exchange.rate.timer.exchange",
            queueName = "fxs.v1.exchange.rate.timer.queue",
            routingKey = "fxs.v1.exchange.rate.timer.routing-key",
            bindingFunction = Exchange::setupDeadLetterExchange,
            dlx = V1_EXCHANGE_RATE_TIMER_DL_EXCHANGE
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

        private fun setupDeadLetterExchange(rabbitAdmin: RabbitAdmin) {
            val exchange = DirectExchange(exchangeName)
            val queue = QueueBuilder.nonDurable(queueName)
                .withArgument("x-dead-letter-exchange", dlx?.exchangeName)
                .withArgument("x-dead-letter-routing-key", dlx?.routingKey)
                .withArgument("x-message-ttl", 500)
                .build()
            rabbitAdmin.declareExchange(exchange)
            rabbitAdmin.declareQueue(queue)
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey))
        }

    }

}