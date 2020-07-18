package com.luisenricke.botonpanico.receiver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import timber.log.Timber

class DeliverySMSReceiver : BroadcastReceiver() {

    companion object {
        val TAG = DeliverySMSReceiver::class.simpleName
    }

    @SuppressLint("LogNotTimber")
    override fun onReceive(context: Context?, intent: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                Log.i(TAG, "DeliverySMSReceiver -> RESULT_OK")
            }
            Activity.RESULT_CANCELED -> {
                Log.e(TAG, "DeliverySMSReceiver -> RESULT_CANCELED")
            }
            else -> {
                Log.e(TAG, "DeliverySMSReceiver -> WTF")
            }
        }
    }
}
