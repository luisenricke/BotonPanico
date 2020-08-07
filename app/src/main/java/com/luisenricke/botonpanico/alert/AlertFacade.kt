package com.luisenricke.botonpanico.alert

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.luisenricke.androidext.preferenceGet
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.AppDatabase
import com.luisenricke.botonpanico.database.entity.Alert
import com.luisenricke.botonpanico.database.entity.AlertContact
import com.luisenricke.botonpanico.service.LastLocation
import com.luisenricke.botonpanico.service.Vibration

class AlertFacade {

    companion object {
        val TAG = AlertFacade::class.simpleName
    }

    @SuppressLint("LogNotTimber")
    fun sentMessage(context: Context) {
        // TODO Join with coroutine of called
        Looper.prepare()
        val looper = Looper.myLooper()

        val database = AppDatabase.getInstance(context)
        val contactDao = database.contactDAO()
        val alertDAO = database.alertDAO()
        val alertContactDAO = database.alertContactDAO()

        // Contacts
        val contacts = contactDao.getHighlighted()

        if (contacts.isEmpty()) {
            Log.e(TAG, "Empty contacts")
            looper?.quit()
            return
        }

        val defaultMessage = context.preferenceGet(Constraint.ALERT_MESSAGE, String::class)
                             ?: context.getString(R.string.alert_message_default)

        // Vibration
        Vibration.getInstance(context).vibrate()

        // Location
        val location = LastLocation.getInstance(context).getLocation()

        // Build message to send
        val alert = Alert(
                latitude = location?.latitude ?: 0.0,
                longitude = location?.longitude ?: 0.0,
                type = "Sent"
        )

        val idAlert = alertDAO.insert(alert)
        val alertContacts = arrayListOf<AlertContact>()

        for (contact in contacts) {
            val alertContact = AlertContact(alertId = idAlert, contactId = contact.id)
            alertContact.messageSent = if (contact.message.isEmpty()) defaultMessage else contact.message
            alertContacts.add(alertContact)
        }

        alertContactDAO.inserts(alertContacts)

        // Send message
        for (alertContact in alertContacts) {
            val contact = contactDao.get(alertContact.contactId)
            //                SendSMS.getInstance(context).simpleMessage(contact!!.phone, alertContact.messageSent)
            //                SendSMS.getInstance(context).locationMessage(contact!!.phone, location)
        }

        looper?.quit()
    }
}
