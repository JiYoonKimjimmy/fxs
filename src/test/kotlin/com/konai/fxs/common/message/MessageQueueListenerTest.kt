package com.konai.fxs.common.message

import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MessageQueueListenerTest : CustomBehaviorSpec({

    val messageQueueListener = dependencies.messageQueueListener
    val v1TransactionFixture = dependencies.v1TransactionFixture
    val v1TransactionRepository = dependencies.fakeV1TransactionRepository

    given("외화 계좌 거래 내역 저장 Event Message 수신하여") {
        val transaction = v1TransactionFixture.make()
        val message = V1SaveTransactionMessage(transaction)

        `when`("저장 처리 정상인 경우") {
            messageQueueListener.receiveMessage(message)

            then("거래 내역 저장 결과 정상 확인한다") {
                val entity = v1TransactionRepository.findByPredicate(V1TransactionPredicate(id = transaction.id))
                entity!! shouldNotBe null
                entity.id shouldBe transaction.id
            }
        }
    }

})