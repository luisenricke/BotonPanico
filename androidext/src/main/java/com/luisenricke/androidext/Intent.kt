package com.luisenricke.androidext

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

@Suppress("unused")
fun AppCompatActivity.intentSelectContact(requestCode: Int) {
    val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
    this.startActivityForResult(intent, requestCode)
}

@Suppress("unused")
fun Fragment.intentSelectContact(requestCode: Int) {
    val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
    this.startActivityForResult(intent, requestCode)
}

@Suppress("unused")
fun Context.intentEnableLocation() {
    this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}


