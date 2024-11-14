package com.konai.fxs.v1.sequence.service.domain

import com.konai.fxs.common.enumerate.SequenceType

data class V1SequenceGenerator(
    val id: Long? = null,
    val type: SequenceType,
    val date: String,
    var value: Long
) {

    fun increment(): V1SequenceGenerator {
        this.value += 1
        return this
    }

}