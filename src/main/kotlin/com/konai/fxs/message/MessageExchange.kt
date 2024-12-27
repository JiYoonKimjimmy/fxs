package com.konai.fxs.message

import com.konai.fxs.common.EMPTY

enum class MessageQueueExchange(
    val exchangeName: String,
    val routingKey: String = EMPTY,
) {

    V1_SAVE_TRANSACTION_EXCHANGE(
        exchangeName = "fxs.v1.save.transaction.exchange",
        routingKey = "fxs.v1.save.transaction.routing-key",
    ),
    V1_EXPIRE_TRANSACTION_EXCHANGE(
        exchangeName = "fxs.v1.expire.transaction.exchange",
        routingKey = "fxs.v1.expire.transaction.routing-key",
    ),
    V1_EXCHANGE_RATE_TIMER_EXCHANGE(
        exchangeName = "fxs.v1.exchange.rate.timer.exchange",
        routingKey = "fxs.v1.exchange.rate.timer.routing-key",
    )

}

object MessageQueue {
    const val V1_SAVE_TRANSACTION_QUEUE = "fxs.v1.save.transaction.queue"
    const val V1_EXPIRE_TRANSACTION_DL_QUEUE = "fxs.v1.expire.transaction.dl.queue"
    const val V1_EXCHANGE_RATE_TIMER_DL_QUEUE = "fxs.v1.exchange.rate.timer.dl.queue"
}