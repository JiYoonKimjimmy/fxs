package com.konai.fxs.v1.transaction.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Table(name = "V1_TRANSACTIONS")
@Entity(name = "V1Transaction")
class V1TransactionEntity(

    @Id
    @Column(name = "ID")
    override var id: Long?,
    @Column(name = "TR_REFERENCE_ID")
    val trReferenceId: String,
    @Column(name = "CHANNEL")
    val channel: TransactionChannel,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "BASE_ACQUIRER_ID")),
        AttributeOverride(name = "type", column = Column(name = "BASE_ACQUIRER_TYPE")),
        AttributeOverride(name = "name", column = Column(name = "BASE_ACQUIRER_NAME"))
    )
    val baseAcquirer: V1AcquirerEntity,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "TARGET_ACQUIRER_ID")),
        AttributeOverride(name = "type", column = Column(name = "TARGET_ACQUIRER_TYPE")),
        AttributeOverride(name = "name", column = Column(name = "TARGET_ACQUIRER_NAME"))
    )
    val targetAcquirer: V1AcquirerEntity?,
    @Column(name = "TYPE")
    val type: TransactionType,
    @Column(name = "PURPOSE")
    val purpose: TransactionPurpose,
    @Column(name = "STATUS")
    val status: TransactionStatus,
    @Column(name = "CURRENCY")
    val currency: String,
    @Column(name = "AMOUNT")
    val amount: BigDecimal,
    @Column(name = "BEFORE_BALANCE")
    val beforeBalance: BigDecimal,
    @Column(name = "AFTER_BALANCE")
    val afterBalance: BigDecimal,
    @Column(name = "EXCHANGE_RATE")
    val exchangeRate: BigDecimal,
    @Column(name = "TRANSFER_DATE")
    val transferDate: String,
    @Column(name = "COMPLETE_DATE")
    val completeDate: LocalDateTime,
    @Column(name = "CANCEL_DATE")
    val cancelDate: LocalDateTime?,
    @Column(name = "ORG_TRANSACTION_ID")
    val orgTransactionId: Long?,
    @Column(name = "ORG_TR_REFERENCE_ID")
    val orgTrReferenceId: String?,
    @Column(name = "REQUEST_BY")
    val requestBy: String,
    @Column(name = "REQUEST_NOTE")
    val requestNote: String?,

) : SequenceBaseEntity()