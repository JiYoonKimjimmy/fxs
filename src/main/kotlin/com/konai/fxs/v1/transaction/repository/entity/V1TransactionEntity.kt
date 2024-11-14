package com.konai.fxs.v1.transaction.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "V1_TRANSACTIONS")
@Entity(name = "V1Transaction")
class V1TransactionEntity(

    @Id
    @Column(name = "ID")
    override var id: Long?,
    @Column(name = "TR_REFERENCE_ID")
    val trReferenceId: String,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "BASE_ACQUIRER_ID")),
        AttributeOverride(name = "type", column = Column(name = "BASE_ACQUIRER_TYPE")),
        AttributeOverride(name = "name", column = Column(name = "BASE_ACQUIRER_NAME"))
    )
    val acquirer: V1AcquirerEntity,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "TARGET_ACQUIRER_ID")),
        AttributeOverride(name = "type", column = Column(name = "TARGET_ACQUIRER_TYPE")),
        AttributeOverride(name = "name", column = Column(name = "TARGET_ACQUIRER_NAME"))
    )
    val fromAcquirer: V1AcquirerEntity?,
    @Column(name = "TYPE")
    val type: TransactionType,
    @Column(name = "PURPOSE")
    val purpose: TransactionPurpose,
    @Column(name = "CHANNEL")
    val channel: TransactionChannel,
    @Column(name = "CURRENCY")
    val currency: String,
    @Column(name = "AMOUNT")
    val amount: BigDecimal,
    @Column(name = "EXCHANGE_RATE")
    val exchangeRate: BigDecimal,
    @Column(name = "TRANSFER_DATE")
    val transferDate: String,
    @Column(name = "REQUEST_BY")
    val requestBy: String,
    @Column(name = "REQUEST_NOTE")
    val requestNote: String?,
    @Column(name = "STATUS")
    val status: TransactionStatus

) : SequenceBaseEntity()