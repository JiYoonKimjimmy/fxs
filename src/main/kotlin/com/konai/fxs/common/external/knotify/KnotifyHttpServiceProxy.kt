package com.konai.fxs.common.external.knotify

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

interface KnotifyHttpServiceProxy {

    @PostExchange("/api/km/notifications/sms")
    fun sendSMS(@RequestBody request: KnotifyPostSendSmsRequest): ResponseEntity<Void>

}