package com.luisenricke.botonpanico.contacts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.text.Editable
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.textfield.TextInputLayout
import com.luisenricke.androidext.checkPermission
import com.luisenricke.androidext.intentSelectContact
import com.luisenricke.androidext.requestPermission
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.kotlinext.removeWhiteSpaces

@Suppress("unused")
object ContactUtils {

    fun requestContact(fragment: Fragment) {
        if (fragment.checkPermission(Manifest.permission.READ_CONTACTS)) {
            fragment.intentSelectContact(Constraint.INTENT_READ_CONTACTS_CODE)
        } else {
            fragment.requestPermission(
                    Manifest.permission.READ_CONTACTS,
                    Constraint.PERMISSION_READ_CONTACTS_CODE,
                    fragment.getString(R.string.permission_message_read_contacts_denied),
                    true
            )
        }
    }

    fun clearStack(navController: NavController) {
        navController.popBackStack(R.id.nav_contact, true)
        navController.navigate(R.id.nav_contact)
    }

    fun getContact(context: Context, data: Intent?): Contact {
        val contact = Contact()

        if (data!!.data == null) return contact

        val contentResolver = context.contentResolver
        val uri = data.data

        val contacts = contentResolver.query(uri!!, null, null, null, null)
        if (contacts!!.moveToFirst()) {
            val id: String = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID)) ?: return contact

            contact.name = contacts.getStringOrNull(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).let { str ->
                if (!str.isNullOrEmpty() && !str.contains("\\d+".toRegex())) {
                    str
                } else {
                    context.getString(R.string.safety_contact)
                }
            }

            val phones = contentResolver.query(Phone.CONTENT_URI, null, "${Phone.CONTACT_ID} = $id", null, null)

            while (phones!!.moveToNext()) {
                val type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                contact.phone = phones.getStringOrNull(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).takeIf {
                    type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE || type == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE || type == ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
                }.let { str -> str ?: "" }
            }

            phones.close()
        }

        contacts.close()

        return contact
    }

    fun checkDefaultImage(context: Context, image: Drawable): Boolean {
        return image == context.getDrawable(R.drawable.ic_baseline_person_24)
    }

    fun isValidPhone(phone: String): Boolean {
        val trimPhone = phone.removeWhiteSpaces()
        return if (trimPhone.length < 10 || trimPhone.length > 14) false
        else android.util.Patterns.PHONE.matcher(phone).matches()
    }

    // TODO sent to resource of strings
    fun hasError(context: Context, layout: TextInputLayout, editable: Editable): Boolean {
        val isNotEmpty = editable.toString().isNotEmpty()
        val name = layout.hint

        if (isNotEmpty) {
            layout.isErrorEnabled = false
            layout.error = ""
            return false
        }

        layout.isErrorEnabled = true
        layout.error = when (name) {
            context.getString(R.string.contact_name)         -> "Es necesario poner el nombre"
            context.getString(R.string.contact_phone)        -> "Es necesario poner el telefono"
            context.getString(R.string.contact_relationship) -> "Es necesario escoger alguna de las opciones"
            else                                             -> "Es necesario poner el campo"
        }
        return true
    }

    fun hasErrorPhone(layout: TextInputLayout, editable: Editable): Boolean {
        val isNotEmpty = editable.toString().isNotEmpty()

        if (isValidPhone(editable.toString()) && isNotEmpty) {
            layout.isErrorEnabled = false
            layout.error = ""
            return false
        }

        if (!isValidPhone(editable.toString())) {
            layout.isErrorEnabled = true
            layout.error = "Numero invalido"
            return true
        }

        return false
    }
}
