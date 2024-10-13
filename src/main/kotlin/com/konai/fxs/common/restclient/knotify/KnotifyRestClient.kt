package com.konai.fxs.common.restclient.knotify

import com.konai.fxs.common.restclient.BaseRestClient
import com.konai.fxs.common.restclient.ComponentName
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class KnotifyRestClient : BaseRestClient() {

    override val baseUrl: String by lazy { generateBaseUrl(ComponentName.KNOTIFY) }

    fun sendSMS(request: KnotifyPostSendSmsRequest): ResponseEntity<Void> {
        return postBodiless(
            url = "$baseUrl${request.url}",
            body = request
        )
    }

}