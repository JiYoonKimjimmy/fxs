package com.konai.fxs.common.external.knotify

import org.springframework.http.ResponseEntity

interface KnotifyHttpService {

    fun sendSMS(request: KnotifyPostSendSmsRequest): ResponseEntity<Void>

}