package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Table(name = "V1_ACCOUNTS")
@Entity
class V1AccountEntity(

    @Id
    @SequenceGenerator(name = "V1_ACCOUNTS_SEQ", sequenceName = "V1_ACCOUNTS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null,
    @Embedded
    val acquirer: V1AcquirerEntity,
    val currency: String,
    val balance: BigDecimal,
    val minRequiredBalance: BigDecimal,
    val averageExchangeRate: BigDecimal

) : SequenceBaseEntity()