package com.tambal_ban.core.utils

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

object CrashlyticsHelper {

    /**
     * Crash reporting must never itself crash the app (or, in a JVM unit test with no
     * FirebaseApp initialized, throw IllegalStateException) — every call is best-effort.
     */
    private inline fun safe(block: () -> Unit) {
        try {
            block()
        } catch (_: Exception) {
        }
    }

    fun initialize() {
        safe { Firebase.crashlytics.sendUnsentReports() }
    }

    fun setUserId(userId: String) {
        safe { Firebase.crashlytics.setUserId(userId) }
    }

    fun clearUserId() {
        safe { Firebase.crashlytics.setUserId("") }
    }

    fun logNonFatal(throwable: Throwable, message: String? = null) {
        safe {
            if (!message.isNullOrBlank()) {
                Firebase.crashlytics.log(message)
            }
            Firebase.crashlytics.recordException(throwable)
        }
    }

    fun logMessage(message: String) {
        safe { Firebase.crashlytics.log(message) }
    }
}
