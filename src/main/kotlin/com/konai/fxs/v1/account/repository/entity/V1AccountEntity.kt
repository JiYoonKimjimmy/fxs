package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import com.konai.fxs.infra.converter.EncryptionCustomerInfoConverter
import jakarta.persistence.*

@Table(name = "V1_ACCOUNTS")
@Entity
class V1AccountEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null,
    @Convert(converter = EncryptionCustomerInfoConverter::class)
    val accountNumber: String

) : SequenceBaseEntity()