package com.luisenricke.botonpanico.receiver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

class SentSMSReceiver : BroadcastReceiver() {

    companion object {
        val TAG = SentSMSReceiver::class.simpleName
    }

    @SuppressLint("LogNotTimber")
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "SentSMS trigged", Toast.LENGTH_SHORT).show()
        when (resultCode) {
            Activity.RESULT_OK                      -> {
                Log.i(TAG, "SentSMSReceiver -> RESULT_OK")
            }
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                Log.e(TAG, "SentSMSReceiver -> RESULT_ERROR_GENERIC_FAILURE")
            }
            SmsManager.RESULT_ERROR_NO_SERVICE      -> {
                Log.e(TAG, "SentSMSReceiver -> RESULT_ERROR_NO_SERVICE")
            }
            SmsManager.RESULT_ERROR_NULL_PDU        -> {
                Log.e(TAG, "SentSMSReceiver -> RESULT_ERROR_NULL_PDU")
            }
            SmsManager.RESULT_ERROR_RADIO_OFF       -> {
                Log.e(TAG, "SentSMSReceiver -> RESULT_ERROR_RADIO_OFF")
            }
            else                                    -> {
                Log.e(TAG, "SentSMSReceiver -> WTF")
            }
        }
    }
}
