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
fun AppCompatActivity.requestPermission(permission: String, requestCode: Int, deniedMessage: String, displayMessage: Boolean = false) {

    when {
        checkPermission(permission)                            ->
            Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        shouldShowRequestPermissionRationale(this, permission) ->
            requestPermissions(this, arrayOf(permission), requestCode)

        else                                                   ->
            if (displayMessage) toastLong(deniedMessage)

    }
}

@Suppress("unused")
fun AppCompatActivity.requestCriticalPermission(permission: String, requestCode: Int, permissionMessage: PermissionMessage) {
    when {
        checkPermission(permission)                            ->
            Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        shouldShowRequestPermissionRationale(this, permission) ->
            requestPermissions(this, arrayOf(permission), requestCode)

        else                                                   ->
            alertDialog(this, permissionMessage)
    }
}

@Suppress("unused")
fun AppCompatActivity.requestPermissions(permissions: MutableList<String>, requestCode: Int, deniedMessage: String, displayMessage: Boolean = false) {
    val permissionNeeded: MutableList<String> = mutableListOf()
    var shouldAskAgain = false

    for (permission in permissions) {
        if (!checkPermission(permission)) permissionNeeded.add(permission)
        else Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        val checkPermission = shouldShowRequestPermissionRationale(this, permission)

        if (checkPermission) shouldAskAgain = true
    }

    if (permissionNeeded.isEmpty()) return

    if (shouldAskAgain) {
        if (displayMessage) requestPermissions(this, permissions.toTypedArray(), requestCode)
    } else {
        toastLong(deniedMessage)
    }
}

@Suppress("unused")
fun AppCompatActivity.requestCriticalPermissions(permissions: MutableList<String>, requestCode: Int, permissionMessage: PermissionMessage) {
    val permissionNeeded: MutableList<String> = mutableListOf()
    var shouldAskAgain = false

    for (permission in permissions) {
        if (!checkPermission(permission)) permissionNeeded.add(permission)
        else Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        val checkedPermission = shouldShowRequestPermissionRationale(this, permission)

        if (checkedPermission) shouldAskAgain = true
    }

    if (permissionNeeded.isEmpty()) return

    if (shouldAskAgain) requestPermissions(this, permissions.toTypedArray(), requestCode)
    else alertDialog(this, permissionMessage)
}

@Suppress("unused")
fun AppCompatActivity.checkPermission(permission: String): Boolean =
    checkSelfPermission(this, permission) == PERMISSION_GRANTED

@Suppress("unused")
fun AppCompatActivity.checkPermissions(permissions: MutableList<String>): Boolean =
    permissions.all { checkSelfPermission(this, it) == PERMISSION_GRANTED }
// endregion

// region fragment
@Suppress("unused")
fun Fragment.requestPermission(permission: String, requestCode: Int, deniedMessage: String, displayMessage: Boolean = false) {
    val activity = this.activity!!

    when {
        checkPermission(permission)                                ->
            Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        shouldShowRequestPermissionRationale(activity, permission) ->
            requestPermissions(arrayOf(permission), requestCode)

        else                                                       ->
            if (displayMessage) toastLong(deniedMessage)
    }
}

@Suppress("unused")
fun Fragment.requestCriticalPermission(permission: String, requestCode: Int, permissionMessage: PermissionMessage) {
    val context = this.context!!
    val activity = this.activity!!

    when {
        checkPermission(permission)                                ->
            Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        shouldShowRequestPermissionRationale(activity, permission) ->
            requestPermissions(arrayOf(permission), requestCode)

        else                                                       ->
            alertDialog(context, permissionMessage)
    }
}

@Suppress("unused")
fun Fragment.requestPermissions(permissions: MutableList<String>, requestCode: Int, deniedMessage: String, displayMessage: Boolean = false) {
    val activity = this.activity!!

    val permissionNeeded: MutableList<String> = mutableListOf()
    var shouldAskAgain = false

    for (permission in permissions) {
        if (!checkPermission(permission)) permissionNeeded.add(permission)
        else Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        val checkPermission = shouldShowRequestPermissionRationale(activity, permission)

        if (checkPermission) shouldAskAgain = true
    }

    if (permissionNeeded.isEmpty()) return

    if (shouldAskAgain) {
        if (displayMessage) requestPermissions(permissions.toTypedArray(), requestCode)
    } else {
        toastLong(deniedMessage)
    }
}

@Suppress("unused")
fun Fragment.requestCriticalPermissions(permissions: MutableList<String>, requestCode: Int, permissionMessage: PermissionMessage) {
    val context = this.context!!
    val activity = this.activity!!

    val permissionNeeded: MutableList<String> = mutableListOf()
    var shouldAskAgain = false

    for (permission in permissions) {
        if (!checkPermission(permission)) permissionNeeded.add(permission)
        else Log.d(TAG, "${getString(R.string.permission_granted)} of $permission")

        val checkedPermission = shouldShowRequestPermissionRationale(activity, permission)

        if (checkedPermission) shouldAskAgain = true
    }

    if (permissionNeeded.isEmpty()) return

    if (shouldAskAgain) requestPermissions(permissions.toTypedArray(), requestCode)
    else alertDialog(context, permissionMessage)
}

@Suppress("unused")
fun Fragment.checkPermission(permission: String): Boolean =
    checkSelfPermission(this.context!!, permission) == PERMISSION_GRANTED

@Suppress("unused")
fun Fragment.checkPermissions(permissions: MutableList<String>): Boolean =
    permissions.all { checkSelfPermission(this.context!!, it) == PERMISSION_GRANTED }
// endregion

// region context
@Suppress("unused")
@Throws(NullPointerException::class)
fun Context?.checkPermission(permission: String): Boolean {
    if (this == null) throw NullPointerException(EXCEPTION_CONTEXT_NULL)
    return checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

@Suppress("unused")
@Throws(NullPointerException::class)
fun Context?.checkPermissions(permissions: MutableList<String>): Boolean {
    if (this == null) throw NullPointerException(EXCEPTION_CONTEXT_NULL)
    return permissions.all { checkSelfPermission(this, it) == PERMISSION_GRANTED }
}
// endregion

data class PermissionMessage(
        val context: Context,
        var title: String = context.getString(R.string.permissions_title),
        var message: String = context.getString(R.string.permission_message),
        var positiveButton: String = context.getString(R.string.permission_positive_button),
        var negativeButton: String = context.getString(R.string.permission_negative_button),
        var denied: String = context.getString(R.string.permission_denied)
)

private fun alertDialog(context: Context, permissionMessage: PermissionMessage) =
    AlertDialog.Builder(context)
        .setTitle(permissionMessage.title)
        .setMessage(permissionMessage.message)
        .setPositiveButton(permissionMessage.positiveButton) { _: DialogInterface, _: Int ->
            context.intentSettings()
        }
        .setNegativeButton(permissionMessage.negativeButton) { dialog: DialogInterface, _: Int ->
            context.toastShort(permissionMessage.denied)
            dialog.dismiss()
        }
        .show()
