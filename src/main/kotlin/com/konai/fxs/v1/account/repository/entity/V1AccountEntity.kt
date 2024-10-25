package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
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
    @Column(name = "currency")
    val currency: String,
    @Column(name = "balance")
    val balance: BigDecimal,
    @Column(name = "minRequiredBalance")
    val minRequiredBalance: BigDecimal,
    @Column(name = "averageExchangeRate")
    val averageExchangeRate: BigDecimal

) : SequenceBaseEntity()