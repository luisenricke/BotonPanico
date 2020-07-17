package com.luisenricke.androidext

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment

// TODO: make functions to handle list of permission

private const val TAG = "LocationManagerExt"

@Suppress("unused")
fun AppCompatActivity.permissionApply(
    permission: String,
    requestCode: Int,
    message: String = getString(R.string.permission_message),
    denied: String = getString(R.string.permission_denied)
) {
    when {
        checkSelfPermission(this, permission) == PERMISSION_GRANTED -> {
            Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")
        }

        shouldShowRequestPermissionRationale(this, permission) -> {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.permission_positive_button))
                { _: DialogInterface, _: Int ->
                    requestPermissions(this, arrayOf(permission), requestCode)
                }
                .setNegativeButton(getString(R.string.permission_negative_button))
                { dialog: DialogInterface, _: Int ->
                    toastShort(denied)
                    dialog.dismiss()
                }
                .show()
        }

        else -> {
            requestPermissions(this, arrayOf(permission), requestCode)
        }
    }
}

@Suppress("unused")
fun AppCompatActivity.permissionCheck(permission: String): Boolean =
    checkSelfPermission(this, permission) == PERMISSION_GRANTED

@Suppress("unused")
fun Fragment.permissionApply(
    permission: String,
    requestCode: Int,
    message: String = getString(R.string.permission_message),
    denied: String = getString(R.string.permission_denied)
) {
    val context = this.context!!
    val activity = this.activity!!

    when {
        checkSelfPermission(context, permission) == PERMISSION_GRANTED -> {
            Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")
        }

        shouldShowRequestPermissionRationale(activity, permission) -> {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.permission_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.permission_positive_button))
                { _: DialogInterface, _: Int ->
                    requestPermissions(arrayOf(permission), requestCode)
                }
                .setNegativeButton(getString(R.string.permission_negative_button))
                { dialog: DialogInterface, _: Int ->
                    toastShort(denied)
                    dialog.dismiss()
                }
                .show()
        }

        else -> {
            requestPermissions(arrayOf(permission), requestCode)
        }
    }
}

@Suppress("unused")
fun Fragment.permissionCheck(permission: String): Boolean =
    checkSelfPermission(this.context!!, permission) == PERMISSION_GRANTED

@Suppress("unused")
fun Context.permissionApply(
    permission: String,
    requestCode: Int,
    message: String = getString(R.string.permission_message),
    denied: String = getString(R.string.permission_denied)
) {
    val activity = this as AppCompatActivity

    when {
        checkSelfPermission(this, permission) == PERMISSION_GRANTED -> {
            Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")
        }

        shouldShowRequestPermissionRationale(activity, permission) -> {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.permission_positive_button))
                { _: DialogInterface, _: Int ->
                    requestPermissions(activity, arrayOf(permission), requestCode)
                }
                .setNegativeButton(getString(R.string.permission_negative_button))
                { dialog: DialogInterface, _: Int ->
                    toastShort(denied)
                    dialog.dismiss()
                }
                .show()
        }

        else -> {
            requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }
}

@Suppress("unused")
fun Context.permissionCheck(permission: String): Boolean =
    checkSelfPermission(this, permission) == PERMISSION_GRANTED
