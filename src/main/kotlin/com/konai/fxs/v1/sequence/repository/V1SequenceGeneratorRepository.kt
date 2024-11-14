package com.konai.fxs.v1.sequence.repository

import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.v1.sequence.service.domain.V1SequenceGenerator

interface V1SequenceGeneratorRepository {

    fun next(type: SequenceType): V1SequenceGenerator

}