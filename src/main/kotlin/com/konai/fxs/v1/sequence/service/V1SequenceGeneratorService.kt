package com.konai.fxs.v1.sequence.service

import com.konai.fxs.common.enumerate.SequenceType

interface V1SequenceGeneratorService {

    fun next(type: SequenceType): Long

}