package com.luisenricke.botonpanico

import android.app.Application
import com.luisenricke.botonpanico.database.AppDatabase
import timber.log.Timber

class App : Application() {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()

        initTimber()
        initRoom()
    }

    private fun initTimber() {

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    private fun initRoom() {

        if (database.contactDAO().count() <= 0) {
            Timber.i("Empty contact list")

        }

        if (database.alertDAO().count() <= 0) {
            Timber.i("Empty alert list")

        }

    }
}
