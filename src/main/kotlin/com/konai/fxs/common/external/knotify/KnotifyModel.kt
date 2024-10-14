package com.konai.fxs.common.external.knotify

data class KnotifyPostSendSmsRequest(
    val aspId: String,
    val id: String,
    val idType: String,
    val notificationType: String,
    val templateVariables: Map<String, String>
) {

    companion object {

        fun ofWarningBalanceInsufficient(mobileNumber: String = "01012341234"): KnotifyPostSendSmsRequest {
            return KnotifyPostSendSmsRequest(
                aspId = "953365000000000",
                id = mobileNumber,
                idType = "MOBILE",
                notificationType = "CHATBOT_INFO_CHECK",
                templateVariables = mapOf("authCode" to "123456")
            )
        }

    }

}