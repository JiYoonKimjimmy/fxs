package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.v1.sequence.service.V1SequenceGeneratorService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import org.springframework.stereotype.Service

@Service
class V1AccountTransactionServiceImpl(
    private val v1TransactionDepositService: V1TransactionDepositService,
    private val v1TransactionWithdrawalService: V1TransactionWithdrawalService,
    private val v1SequenceGeneratorService: V1SequenceGeneratorService
) : V1AccountTransactionService {

    override fun manualDeposit(transaction: V1Transaction): V1Transaction {
        return transaction
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .let(v1TransactionDepositService::deposit)
    }

    override fun manualWithdrawal(transaction: V1Transaction): V1Transaction {
        return transaction
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .let(v1TransactionWithdrawalService::withdrawal)
    }

    override fun withdrawalPending(transaction: V1Transaction): V1Transaction {
        return transaction
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .let(v1TransactionWithdrawalService::pending)
    }

    override fun withdrawalComplete(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        return v1TransactionWithdrawalService.complete(trReferenceId, channel)
    }

    override fun withdrawalCancel(trReferenceId: String, orgTrReferenceId: String, channel: TransactionChannel): V1Transaction {
        return v1TransactionWithdrawalService.cancel(
            trReferenceId = trReferenceId,
            orgTrReferenceId = orgTrReferenceId,
            channel = channel,
            canceledTransactionId = v1SequenceGeneratorService::nextTransactionSequence
        )
    }

    override fun reverse(transaction: V1Transaction): V1Transaction {
        return transaction.toReversed()
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .let {
                when (it.type) {
                    TransactionType.DEPOSIT -> manualDeposit(it)
                    TransactionType.WITHDRAWAL -> manualWithdrawal(it)
                }
            }
    }
}