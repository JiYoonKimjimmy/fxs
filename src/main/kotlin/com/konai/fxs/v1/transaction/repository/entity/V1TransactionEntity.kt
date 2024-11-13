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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "V1_TRANSACTIONS_SEQ")
    @SequenceGenerator(name = "V1_TRANSACTIONS_SEQ", sequenceName = "V1_TRANSACTIONS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    override var id: Long? = null,
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
    @Column(name = "Type")
    val type: TransactionType,
    @Column(name = "Purpose")
    val purpose: TransactionPurpose,
    @Column(name = "Channel")
    val channel: TransactionChannel,
    @Column(name = "Currency")
    val currency: String,
    @Column(name = "Amount")
    val amount: BigDecimal,
    @Column(name = "EXCHANGE_RATE")
    val exchangeRate: BigDecimal,
    @Column(name = "TRANSFER_DATE")
    val transferDate: String,
    @Column(name = "REQUEST_BY")
    val requestBy: String,
    @Column(name = "REQUEST_NOTE")
    val requestNote: String?,
    @Column(name = "Status")
    val status: TransactionStatus

) : SequenceBaseEntity()