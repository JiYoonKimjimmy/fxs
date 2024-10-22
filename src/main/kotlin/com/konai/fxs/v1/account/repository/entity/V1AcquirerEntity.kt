package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.enumerate.AcquirerType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

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