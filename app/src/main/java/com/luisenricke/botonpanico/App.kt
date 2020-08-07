package com.luisenricke.botonpanico

import android.app.Application
import com.luisenricke.androidext.preferenceGet
import com.luisenricke.androidext.preferenceSet
import com.luisenricke.botonpanico.database.AppDatabase
import com.luisenricke.botonpanico.database.entity.Alert
import com.luisenricke.botonpanico.database.entity.Contact
import timber.log.Timber

class App : Application() {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()

        initTimber()
        initRoom()
        initDefaultValues()
    }

    private fun initDefaultValues() {
        val alertMessage = preferenceGet(Constraint.ALERT_MESSAGE, String::class)
        if (alertMessage == null || alertMessage.isEmpty()) {
            preferenceSet(Constraint.ALERT_MESSAGE, getString(R.string.alert_message_default))
        }
    }

    private fun initTimber() {

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    private fun initRoom() {

        if (database.contactDAO().count() <= 0) {
            Timber.i("Empty contact list")

            val contact = Contact(name = "Luis", phone = "123456789", relationship = "Family", message = "", isHighlighted = false, image = "")

            database.contactDAO().insert(contact)

            val all = database.contactDAO().get()
            Timber.i(all.toString())
        }

        if (database.alertDAO().count() <= 0) {
            Timber.i("Empty alert list")

            val alert = Alert(latitude = 00.0, longitude = 00.0, type = "Enviado")

            database.alertDAO().insert(alert)
        }
    }
}
