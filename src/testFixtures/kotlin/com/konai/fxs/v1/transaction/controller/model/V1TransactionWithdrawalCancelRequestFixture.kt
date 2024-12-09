package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder

class V1TransactionWithdrawalCancelRequestFixture {

    fun make(
        trReferenceId: String,
        orgTrReferenceId: String,
        channel: TransactionChannel
    ): V1TransactionWithdrawalCancelRequest {
        return fixtureMonkey.giveMeKotlinBuilder<V1TransactionWithdrawalCancelRequest>()
            .setExp(V1TransactionWithdrawalCancelRequest::trReferenceId, trReferenceId)
            .setExp(V1TransactionWithdrawalCancelRequest::orgTrReferenceId, orgTrReferenceId)
            .setExp(V1TransactionWithdrawalCancelRequest::channel, channel)
            .sample()
    }

}