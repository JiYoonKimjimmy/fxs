package com.konai.fxs.v1.sequence.service

interface V1SequenceGeneratorService {

    fun nextTransactionSequence(): Long

}