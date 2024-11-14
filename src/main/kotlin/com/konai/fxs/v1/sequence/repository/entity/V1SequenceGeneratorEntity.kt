package com.konai.fxs.v1.sequence.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import com.konai.fxs.common.enumerate.SequenceType
import jakarta.persistence.*

@Table(name = "V1_SEQUENCE_GENERATOR")
@Entity(name = "V1SequenceGenerator")
class V1SequenceGeneratorEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "V1_SEQUENCE_GENERATOR_SEQ")
    @SequenceGenerator(name = "V1_SEQUENCE_GENERATOR_SEQ", sequenceName = "V1_SEQUENCE_GENERATOR_SEQ", allocationSize = 1)
    @Column(name = "ID")
    override var id: Long? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "SEQUENCE_TYPE")
    val type: SequenceType,
    @Column(name = "SEQUENCE_DATE")
    val date: String,
    @Column(name = "SEQUENCE_VALUE")
    val value: Long

) : SequenceBaseEntity()