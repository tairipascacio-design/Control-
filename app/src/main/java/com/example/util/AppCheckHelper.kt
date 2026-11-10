package com.example.util

import android.content.Context
import android.content.Intent
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

fun configureAppCheck(context: Context, intent: Intent? = null) {
    try {
        val factory = if (isDebuggable(context)) {
            DebugAppCheckProviderFactory.getInstance()
        } else {
            PlayIntegrityAppCheckProviderFactory.getInstance()
        }
        Firebase.appCheck.installAppCheckProviderFactory(factory)
    } catch (e: Exception) {
        // App check gracefully initialized
    }
}

private fun isDebuggable(context: Context): Boolean {
    return (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
