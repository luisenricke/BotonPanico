package com.luisenricke.botonpanico

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import com.luisenricke.androidext.permissionApply
import com.luisenricke.androidext.permissionCheck
import timber.log.Timber

// https://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-on-a?lq=1
// https://stackoverflow.com/questions/20210565/android-location-manager-get-gps-location-if-no-gps-then-get-to-network-provid
class LocationTrack(private val context: Context) : Service(), LocationListener {

    private var location: Location? = null

    private val manager: LocationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    private val isGPSAvailable: Boolean
        get() = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private val isNetworkAvailable: Boolean
        get() = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    val isProvidersAvailable: Boolean
        get() = (isGPSAvailable && isNetworkAvailable)
            .also { Timber.e("gps: ${isGPSAvailable} && network: ${isNetworkAvailable}") }

    val latitude: Double
        get() = location?.latitude ?: 0.0

    val longitude: Double
        get() = location?.longitude ?: 0.0


    @SuppressLint("MissingPermission")
    fun process() {
        isGPSAvailable.takeIf { !it }
            .also { Timber.e(context.getString(R.string.gps_provider_disable)) }

        isNetworkAvailable.takeIf { !it }
            .also { Timber.e(context.getString(R.string.network_provider_disable)) }

       // if (!isProvidersAvailable) return

        if (!context.permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION)
            && !context.permissionCheck(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            Timber.i("request permission")
            context.permissionApply(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Constraint.PERMISSION_ACCESS_FINE_LOCATION_CODE,
                context.getString(R.string.permission_access_fine_location_apply_message),
                context.getString(R.string.permission_access_fine_location_apply_denied)
            )
        } else {
            Timber.i("request location")
            manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                this
            )

            location = when{
                isGPSAvailable -> manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                isNetworkAvailable -> manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                else -> null
            }
        }
    }

    fun stopListener() {
        manager.removeUpdates(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location?) {}
    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES = 1000 * 60 * 1.toLong()
    }
}
