package com.luisenricke.botonpanico

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import com.luisenricke.androidext.*
import com.luisenricke.botonpanico.service.SensorForeground
import timber.log.Timber

@Suppress("unused")
abstract class BaseFragment : Fragment() {

    fun getActivityContext(): MainActivity = (activity as MainActivity)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty()) {
            Timber.e(getString(R.string.on_request_permissions_result_grant_results))
            return
        }

        when (requestCode) {
            Constraint.PERMISSION_READ_CONTACTS_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i(getString(R.string.permission_read_contacts_granted))
                    intentSelectContact(Constraint.INTENT_READ_CONTACTS_CODE)
                } else {
                    Timber.e(getString(R.string.permission_read_contacts_denied))
                }
                return
            }

            Constraint.PERMISSION_GROUP_MAKE_ALERT -> {
                val isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                Timber.i("onRequestPermissionsResult $isGranted")
                if (isGranted) {
                    Timber.i(getString(R.string.permissions_make_alert_granted))
                    SensorForeground.startService(getActivityContext())
                } else {
                    Timber.e(getString(R.string.permissions_make_alert_denied))
                }
                return
            }

            else -> Timber.e(getString(R.string.on_request_permissions_result_doesnt_find_permission))
        }
    }

    fun checkLocationPermission() {
        if (!permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION))
            permissionApply(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Constraint.PERMISSION_ACCESS_FINE_LOCATION_CODE,
                getString(R.string.permission_access_fine_location_apply_message),
                getString(R.string.permission_access_fine_location_apply_denied)
            )
    }

    fun checkAlertPermissions() {
        val alertPermissions = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )

        if (!permissionsCheck(alertPermissions))
            permissionsApply(
                alertPermissions,
                Constraint.PERMISSION_GROUP_MAKE_ALERT,
                getString(R.string.permissions_make_alert_apply_message),
                getString(R.string.permissions_make_alert_apply_denied)
            )
    }
}
