package com.luisenricke.botonpanico.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.luisenricke.androidext.checkProviders
import com.luisenricke.androidext.getBestProvider
import com.luisenricke.androidext.permissionCheck
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.SingletonHolder
import timber.log.Timber

@Suppress("unused")
class LastLocation private constructor(private val context: Context) {

    companion object : SingletonHolder<LastLocation, Context>(::LastLocation) {
        private const val MIN_TIME: Long = 1000L * 60L * 1L // milliseconds * seconds * minutes
        private const val MIN_DISTANCE: Float = 10f         // meters
    }

    //    private lateinit var context: Context
    private val manager: LocationManager = context
        .getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val bestProvider: String?
        get() = manager.getBestProvider()

    private val isPermissionEnable: Boolean
        get() = context.permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION)

    private val listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {}
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}
    }

    // TODO: delete this when finish class
    init {
        manager.checkProviders(context)
        Timber.i("provider: $bestProvider")
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        var location: Location? = null

        if (!isPermissionEnable) Timber.e(context.getString(R.string.permission_access_fine_location_denied))
            .also { return location }

        if (bestProvider == null) Timber.e(context.getString(R.string.location_provider_null))
            .also { return location }

//        manager.requestLocationUpdates(bestProvider, MIN_TIME, MIN_DISTANCE, listener)
        manager.requestSingleUpdate(bestProvider!!, listener, null)
        location = manager.getLastKnownLocation(bestProvider!!)
        Timber.i("lat: ${location?.latitude}, lon: ${location?.longitude}")

        return location
    }

    fun removeUpdate() {
        manager.removeUpdates(listener)
    }
}
