package com.tambal_ban.core.utils

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

object CrashlyticsHelper {

    fun initialize() {
        Firebase.crashlytics.apply {
            sendUnsentReports()
        }
    }

    fun setUserId(userId: String) {
        Firebase.crashlytics.setUserId(userId)
    }

    fun clearUserId() {
        Firebase.crashlytics.setUserId("")
    }

    fun logNonFatal(throwable: Throwable, message: String? = null) {
        if (!message.isNullOrBlank()) {
            Firebase.crashlytics.log(message)
        }
        Firebase.crashlytics.recordException(throwable)
    }

    fun logMessage(message: String) {
        Firebase.crashlytics.log(message)
    }
}
