package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder

class V1TransactionWithdrawalCompleteRequestFixture {

    fun make(
        trReferenceId: String,
        channel: TransactionChannel
    ): V1TransactionWithdrawalCompleteRequest {
        return fixtureMonkey.giveMeKotlinBuilder<V1TransactionWithdrawalCompleteRequest>()
            .setExp(V1TransactionWithdrawalCompleteRequest::trReferenceId, trReferenceId)
            .setExp(V1TransactionWithdrawalCompleteRequest::channel, channel)
            .sample()
    }

}