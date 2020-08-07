package com.luisenricke.botonpanico.alert

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.luisenricke.androidext.preferenceGet
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.AppDatabase
import com.luisenricke.botonpanico.database.entity.Alert
import com.luisenricke.botonpanico.database.entity.AlertContact
import com.luisenricke.botonpanico.service.LastLocation
import com.luisenricke.botonpanico.service.SendSMS
import com.luisenricke.botonpanico.service.Vibration
import timber.log.Timber

class AlertFacade() {

    companion object {
        val TAG = AlertFacade::class.simpleName
    }

    @SuppressLint("LogNotTimber")
    fun sentMessage(context: Context) {
        // TODO Fix this
        Looper.prepare() // Wtf with this ?
        val looper = Looper.myLooper()

        val database = AppDatabase.getInstance(context)
        val contactDao = database.contactDAO()
        val alertDAO = database.alertDAO()
        val alertContactDAO = database.alertContactDAO()
        Log.i(TAG, "Database settings")

        // Contacts
        val contacts = contactDao.getHighlighted()
        Log.i(TAG, "contacts highlighted: ${contacts.toString()}")

        if (contacts.isEmpty()) {
            Timber.i("Empty contacts")
        } else {

            val defaultMessage = context.preferenceGet(Constraint.ALERT_MESSAGE, String::class) ?: context.getString(R.string.alert_message_default)
            Log.i(TAG, "default message: $defaultMessage")

            // Vibration
            Vibration.getInstance(context).vibrate()
            Log.i(TAG, "Vibrate")

            // Location
            val location = LastLocation.getInstance(context).getLocation()
            Timber.i("${location?.latitude}, ${location?.longitude}")

            // Build message to send
            val alert = Alert(latitude = 0.0, longitude = 0.0, type = "Sent")
            val idAlert = alertDAO.insert(alert)

            val alertContacts = arrayListOf<AlertContact>()

            for (contact in contacts) {
                val alertContact = AlertContact(alertId = idAlert!!, contactId = contact.id)
                alertContact.messageSent = if (contact.message.isEmpty()) defaultMessage else contact.message
                alertContacts.add(alertContact)
            }
            alertContactDAO.inserts(alertContacts)

            Log.i(TAG, "Build message")

            // Send message
            for (alertContact in alertContacts) {
                //                Timber.i("Send message: ${alertContact.messageSent}")
                Log.i(TAG, "send ${alertContact.messageSent}")
                val contact = contactDao.get(alertContact.contactId)
//                SendSMS.getInstance(context).simpleMessage(contact!!.phone, alertContact.messageSent)
//                SendSMS.getInstance(context).locationMessage(contact!!.phone, location)
            }

            looper?.quit()
        }
    }


}
