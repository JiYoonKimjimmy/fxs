package com.konai.fxs.common.message

import com.konai.fxs.common.EMPTY

enum class MessageQueueExchange(
    val exchangeName: String,
    val routingKey: String = EMPTY,
) {

    V1_SAVE_TRANSACTION_EXCHANGE(
        exchangeName = "fxs.v1.save.transaction.exchange",
        routingKey = "fxs.v1.save.transaction.routingKey",
    ),
    V1_EXPIRE_PREPARED_TRANSACTION_EXCHANGE(
        exchangeName = "fxs.v1.expire.prepared.transaction.exchange",
        routingKey = "fxs.v1.expire.prepared.transaction.routingKey",
    )

}

object MessageQueue {
    const val V1_SAVE_TRANSACTION_QUEUE = "fxs.v1.save.transaction.queue"
    const val V1_EXPIRE_PREPARED_TRANSACTION_DL_QUEUE = "fxs.v1.expire.prepared.transaction.dl.queue"
}