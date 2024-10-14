package com.konai.fxs.common.external.knotify

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class KnotifyHttpServiceImpl(
    private val knotifyHttpServiceProxy: KnotifyHttpServiceProxy
) : KnotifyHttpService {

    override fun sendSMS(request: KnotifyPostSendSmsRequest): ResponseEntity<Void> {
        return knotifyHttpServiceProxy.sendSMS(request)
    }

}