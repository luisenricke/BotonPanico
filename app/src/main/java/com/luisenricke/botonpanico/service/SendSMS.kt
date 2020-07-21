package com.luisenricke.botonpanico.service

import android.content.Context
import android.location.Location
import android.telephony.SmsManager
import com.luisenricke.botonpanico.SingletonHolder
import com.luisenricke.botonpanico.receiver.DeliverySMSReceiver
import com.luisenricke.botonpanico.receiver.SentSMSReceiver
import com.luisenricke.kotlinext.roundDecimals

// https://stackoverflow.com/questions/26893796/how-to-set-emoji-by-unicode-in-a-textview
// https://stackoverflow.com/questions/4967448/show-compose-sms-view-in-android
// https://stackoverflow.com/questions/18771356/check-if-an-sms-is-actually-sent
// TODO: Fix this with broadcast receiver [SentSMSReceiver, DeliverySMSReceiver]
// TODO: check if it is possible to send messages
@Suppress("unused")
class SendSMS private constructor(private val context: Context) {

    companion object : SingletonHolder<SendSMS, Context>(::SendSMS) {
        const val GOOGLE_MAPS = "http://maps.google.com/?q=" // latitude , longitude
        @JvmStatic
        var pin = String(Character.toChars(0x1F4CD))

        const val INTENT_SENT = "SMS_SENT"
        const val INTENT_DELIVERY = "SMS_DELIVERED"
    }

    private val manager: SmsManager = SmsManager.getDefault()

    private lateinit var sentBroadcastReceiver: SentSMSReceiver
    private lateinit var deliveryBroadcastReceiver: DeliverySMSReceiver

    fun simpleMessage(phone: String, message: String) {
        manager.sendTextMessage(phone, null, message, null, null)
    }

    fun locationMessage(phone: String, location: Location?) {
        if (location == null) return

        val latitude = location.latitude
        val longitude = location.longitude
        val message = "$pin: $GOOGLE_MAPS${latitude.roundDecimals(6)},${longitude.roundDecimals(6)}"

        manager.sendTextMessage(phone, null, message, null, null)
    }

    fun unregisterReceivers() {
        context.unregisterReceiver(sentBroadcastReceiver)
        context.unregisterReceiver(deliveryBroadcastReceiver)
    }
}
