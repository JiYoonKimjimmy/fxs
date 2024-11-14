package com.konai.fxs.v1.sequence.service

import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.v1.sequence.repository.V1SequenceGeneratorRepository
import org.springframework.stereotype.Service

@Service
class V1SequenceGeneratorServiceImpl(
    private val v1SequenceGeneratorRepository: V1SequenceGeneratorRepository
) : V1SequenceGeneratorService {

    override fun next(type: SequenceType): Long {
        return v1SequenceGeneratorRepository.next(type).value
    }

}