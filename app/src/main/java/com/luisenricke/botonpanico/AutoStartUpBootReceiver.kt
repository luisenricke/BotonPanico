package com.luisenricke.botonpanico

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AutoStartUpBootReceiver : BroadcastReceiver() {

    companion object {
        val TAG = AutoStartUpBootReceiver::class.simpleName
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")

        val service = SensorForeground::class.java

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, service))
            } else {
                context.startService(Intent(context, service))
            }
        }
    }
}

