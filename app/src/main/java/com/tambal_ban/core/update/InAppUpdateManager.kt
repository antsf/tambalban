package com.tambal_ban.core.update

import android.app.Activity
import android.content.IntentSender
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.tambal_ban.core.utils.AuthPrefs

class InAppUpdateManager(
    private val authPrefs: AuthPrefs
) {
    companion object {
        private const val REQUEST_CODE_UPDATE = 9001
        private const val DAYS_MILLIS = 3L * 24 * 60 * 60 * 1000
    }

    fun checkForUpdate(activity: Activity) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) return@addOnSuccessListener
            if (!info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) return@addOnSuccessListener

            val lastPrompt = authPrefs.getLastUpdatePromptTime()
            if (lastPrompt > 0 && System.currentTimeMillis() - lastPrompt < DAYS_MILLIS) return@addOnSuccessListener

            authPrefs.setLastUpdatePromptTime(System.currentTimeMillis())

            try {
                appUpdateManager.startUpdateFlow(
                    info,
                    activity,
                    AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
                )
            } catch (_: IntentSender.SendIntentException) {
                // User dismissed or Play Store unavailable
            }
        }.addOnFailureListener {
            // Play Store unavailable (emulator, sideload) — silently ignore
        }

        appUpdateManager.registerListener { state ->
            if (state.installStatus() == com.google.android.play.core.install.model.InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }

    fun completeUpdate(activity: Activity) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlow(
                        info,
                        activity,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                    )
                } catch (_: IntentSender.SendIntentException) { }
            }
        }
    }
}
