package com.luisenricke.botonpanico

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
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
                    intentSelectContact()
                } else {
                    Timber.e(getString(R.string.permission_read_contacts_denied))
                }
                return
            }
            else -> {
                Timber.e(getString(R.string.on_request_permissions_result_doesnt_find_permission))
            }
        }
    }
}
