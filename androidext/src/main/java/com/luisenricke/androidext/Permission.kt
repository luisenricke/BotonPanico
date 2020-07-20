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

private const val TAG = "PermissionExt"
private const val EXCEPTION_CONTEXT_NULL = "The context is null"

// region AppCompatActivity
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
fun AppCompatActivity.permissionsApply(
    permissions: MutableList<String>,
    requestCode: Int,
    message: String = getString(R.string.permission_message),
    denied: String = getString(R.string.permission_denied)
) {
    val neededPermissions: MutableList<String> = mutableListOf()
    var ifPermissionsAsked = false

    for (permission in permissions) {
        if (!permissionCheck(permission)) neededPermissions.add(permission)
        else Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        if (shouldShowRequestPermissionRationale(this, permission)) {
            ifPermissionsAsked = true
        }
    }

    if (neededPermissions.isEmpty()) {
        Log.d(TAG, getString(R.string.permissions_granted))
        return
    }

    if (ifPermissionsAsked) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permissions_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.permission_positive_button))
            { _: DialogInterface, _: Int ->
                requestPermissions(this, neededPermissions.toTypedArray(), requestCode)
            }
            .setNegativeButton(getString(R.string.permission_negative_button))
            { dialog: DialogInterface, _: Int ->
                toastShort(denied)
                dialog.dismiss()
            }
            .show()
    } else {
        requestPermissions(this, permissions.toTypedArray(), requestCode)
    }
}

@Suppress("unused")
fun AppCompatActivity.permissionsCheck(permissions: MutableList<String>): Boolean =
    permissions.all { checkSelfPermission(this, it) == PERMISSION_GRANTED }
// endregion

// region fragment
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
fun Fragment.permissionsApply(
    permissions: MutableList<String>,
    requestCode: Int,
    message: String = getString(R.string.permission_message),
    denied: String = getString(R.string.permission_denied)
) {
    val context = this.context!!
    val activity = this.activity!!

    val neededPermissions: MutableList<String> = mutableListOf()
    var ifPermissionsAsked = false

    for (permission in permissions) {
        if (!permissionCheck(permission)) neededPermissions.add(permission)
        else Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        if (shouldShowRequestPermissionRationale(activity, permission)) {
            ifPermissionsAsked = true
        }
    }

    if (neededPermissions.isEmpty()) {
        Log.d(TAG, getString(R.string.permissions_granted))
        return
    }

    if (ifPermissionsAsked) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.permissions_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.permission_positive_button))
            { _: DialogInterface, _: Int ->
                requestPermissions(neededPermissions.toTypedArray(), requestCode)
            }
            .setNegativeButton(getString(R.string.permission_negative_button))
            { dialog: DialogInterface, _: Int ->
                toastShort(denied)
                dialog.dismiss()
            }
            .show()

    } else {
        requestPermissions(permissions.toTypedArray(), requestCode)
    }
}

@Suppress("unused")
fun Fragment.permissionsCheck(permissions: MutableList<String>): Boolean =
    permissions.all { checkSelfPermission(this.context!!, it) == PERMISSION_GRANTED }
// endregion

// region context
@Suppress("unused")
@Throws(NullPointerException::class)
fun Context?.permissionCheck(permission: String): Boolean {
    if (this == null) throw NullPointerException(EXCEPTION_CONTEXT_NULL)
    return checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

@Suppress("unused")
@Throws(NullPointerException::class)
fun Context?.permissionsCheck(permissions: List<String>): Boolean {
    if (this == null) throw NullPointerException(EXCEPTION_CONTEXT_NULL)
    return permissions.all { checkSelfPermission(this, it) == PERMISSION_GRANTED }
}
// endregion
