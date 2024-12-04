package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.konai.fxs.testsupport.TestExtensionFunctions.toModel
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder

class V1TransactionWithdrawalCompleteRequestFixture {

    fun make(
        acquirer: V1Acquirer,
        trReferenceId: String
    ): V1TransactionWithdrawalCompleteRequest {
        return fixtureMonkey.giveMeKotlinBuilder<V1TransactionWithdrawalCompleteRequest>()
            .setExp(V1TransactionWithdrawalCompleteRequest::acquirer, acquirer.toModel())
            .setExp(V1TransactionWithdrawalCompleteRequest::trReferenceId, trReferenceId)
            .sample()
    }

}