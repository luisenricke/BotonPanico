package com.luisenricke.botonpanico.service

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import com.luisenricke.botonpanico.R
import timber.log.Timber

// https://stackoverflow.com/questions/6775257/android-location-providers-gps-or-network-provider
// https://developerlife.com/2010/10/20/gps/
class LocationIntentService : IntentService("LocationIntentService") {

    companion object {
        @JvmStatic
        fun startService(context: Context) {
            val intent = Intent(context, LocationIntentService::class.java)
            context.startService(intent)
        }

        var isGPSAvailable = false
        var isNetworkAvailable = false
        var isPassiveNetworkAvailable = false
    }

    private lateinit var context: Context
    private lateinit var manager: LocationManager
    private var bestProvider: String? = null

    val criteria = Criteria().apply {
        accuracy = Criteria.ACCURACY_FINE
        isAltitudeRequired = false
        isBearingRequired = false
        isSpeedRequired = false
        isCostAllowed = true
        powerRequirement = Criteria.POWER_HIGH
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        isGPSAvailable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkAvailable = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        isPassiveNetworkAvailable = manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)



        bestProvider = manager.getBestProvider(criteria, true)
        Timber.i("gps: $isGPSAvailable, network: $isNetworkAvailable, passive: $isPassiveNetworkAvailable")

        isGPSAvailable.let { if (!it) Timber.e(context.getString(R.string.gps_provider_disable)) }

        isNetworkAvailable.let { if (!it) Timber.e(context.getString(R.string.network_provider_disable)) }

        // TODO: Generate String
        isPassiveNetworkAvailable.let { if (!it) Timber.e("Passive provider don't available") }
    }

    override fun onHandleIntent(intent: Intent?) {
        Timber.i("Provider: $bestProvider")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        //super.onStart(intent, startId)
        onHandleIntent(intent!!)
        stopSelf()
    }
}
