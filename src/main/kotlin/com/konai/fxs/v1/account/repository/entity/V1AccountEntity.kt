package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "V1_ACCOUNTS")
@Entity(name = "V1Account")
class V1AccountEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "V1_ACCOUNTS_SEQ")
    @SequenceGenerator(name = "V1_ACCOUNTS_SEQ", sequenceName = "V1_ACCOUNTS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    override var id: Long? = null,
    @Embedded
    val acquirer: V1AcquirerEntity,
    @Column(name = "CURRENCY")
    val currency: String,
    @Column(name = "BALANCE")
    val balance: BigDecimal,
    @Column(name = "MIN_REQUIRED_BALANCE")
    val minRequiredBalance: BigDecimal,
    @Column(name = "AVERAGE_EXCHANGE_RATE")
    val averageExchangeRate: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    val status: AccountStatus

) : SequenceBaseEntity() {

    @Embeddable
    class V1AcquirerEntity(
        @Column(name = "ACQUIRER_ID")
        val id: String,
        @Enumerated(EnumType.STRING)
        @Column(name = "ACQUIRER_TYPE")
        val type: AcquirerType,
        @Column(name = "ACQUIRER_NAME")
        val name: String,
    )

}