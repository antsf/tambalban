package com.tambal_ban.core.utils

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object AnalyticsHelper {

    private var analytics: FirebaseAnalytics? = null

    fun initialize() {
        analytics = Firebase.analytics
    }

    fun logScreenView(activity: Activity, screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.localClassName)
        }
        analytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        analytics?.logEvent(eventName, bundle)
    }

    fun logLogin(method: String = "email") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        analytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    fun logSignUp(method: String = "email") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        analytics?.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }

    fun logSearch(term: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, term)
        }
        analytics?.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }

    fun logShare(contentType: String = "app") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        }
        analytics?.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }
}
