package com.respira.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Finds the nearest host [Activity] from the given [Context].
 */
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Keeps the device screen awake and active while [active] is true.
 *
 * When [active] is true, the FLAG_KEEP_SCREEN_ON flag is added to the host Activity window.
 * When [active] is false, or when this Composable leaves the composition (e.g. session end or dismissal),
 * the flag is cleanly removed so the device can resume normal display timeout behavior.
 */
@Composable
fun KeepScreenOn(active: Boolean = true) {
    val context = LocalContext.current
    DisposableEffect(active) {
        val window = context.findActivity()?.window
        if (active) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
