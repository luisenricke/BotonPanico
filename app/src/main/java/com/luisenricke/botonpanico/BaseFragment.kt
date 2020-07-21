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
                val isGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (isGranted) {
                    intentSelectContact(Constraint.INTENT_READ_CONTACTS_CODE)
                } else {
                    Timber.e("${getString(R.string.on_request_permissions_result_denied_permissions)} READ_CONTACTS")
                }

                return
            }

            Constraint.PERMISSION_GROUP_ALERT -> {
                val isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                if (isGranted) {
                    SensorForeground.startService(getActivityContext())
                } else {
                    Timber.e("${getString(R.string.on_request_permissions_result_denied_permissions)} ALERT")
                }

                return
            }

            else -> Timber.e(getString(R.string.on_request_permissions_result_doesnt_find_permission))
        }

    }

    fun checkContactsPermission() {

        if (checkPermission(Manifest.permission.READ_CONTACTS)) {
            intentSelectContact(Constraint.INTENT_READ_CONTACTS_CODE)
        } else {
            requestPermission(
                Manifest.permission.READ_CONTACTS,
                Constraint.PERMISSION_READ_CONTACTS_CODE,
                getString(R.string.permission_message_read_contacts_denied),
                true
            )
        }

    }

    fun checkAlertPermissions() {
        val permissions = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )

        val message = PermissionMessage(
            context = getActivityContext(),
            title = getString(R.string.permission_message_alert_title),
            message = getString(R.string.permission_message_alert_message),
            positiveButton = getString(R.string.permission_message_alert_positive_button),
            negativeButton = getString(R.string.permission_message_alert_negative_button),
            denied = getString(R.string.permission_message_alert_denied)
        )

        if (checkPermissions(permissions)) {
            SensorForeground.startService(getActivityContext())
        } else {
            requestCriticalPermissions(permissions, Constraint.PERMISSION_GROUP_ALERT, message)
        }

    }
}
