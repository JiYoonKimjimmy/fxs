package com.konai.fxs.v1.exchangerate.koreaexim.repository.entity

import com.konai.fxs.common.entity.SequenceBaseEntity
import jakarta.persistence.*

@Table(name = "V1_KOREAEXIM_EXCHANGE_RATE")
@Entity(name = "V1KoreaeximExchangeRate")
class V1KoreaeximExchangeRateEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "V1_KOREAEXIM_EXCHANGE_RATE_SEQ")
    @SequenceGenerator(name = "V1_KOREAEXIM_EXCHANGE_RATE_SEQ", sequenceName = "V1_KOREAEXIM_EXCHANGE_RATE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    override var id: Long? = null,
    @Column(name = "REGISTER_INDEX")
    val index: Int,
    @Column(name = "REGISTER_DATE")
    val registerDate: String,
    @Column(name = "RESULT")
    val result: Int,
    @Column(name = "CUR_UNIT")
    val curUnit: String,
    @Column(name = "CUR_NM")
    val curNm: String,
    @Column(name = "TTB")
    val ttb: String,
    @Column(name = "TTS")
    val tts: String,
    @Column(name = "DEAL_BAS_R")
    val dealBasR: String,
    @Column(name = "BKPR")
    val bkpr: String,
    @Column(name = "YY_EFEE_R")
    val yyEfeeR: String,
    @Column(name = "TEN_DD_EFEE_R")
    val tenDdEfeeR: String,
    @Column(name = "KFTC_DEAL_BAS_R")
    val kftcDealBasR: String,
    @Column(name = "KFTC_BKPR")
    val kftcBkpr: String

) : SequenceBaseEntity()