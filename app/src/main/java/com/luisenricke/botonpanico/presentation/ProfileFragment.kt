package com.luisenricke.botonpanico.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.database.getStringOrNull
import com.luisenricke.androidext.permissionApply
import com.luisenricke.androidext.permissionCheck
import com.luisenricke.androidext.preferenceSet
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentProfileBinding
import com.luisenricke.botonpanico.intentSelectContact
import timber.log.Timber

class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.apply {
            contact.setOnClickListener {

                if (!permissionCheck(Manifest.permission.READ_CONTACTS)) {
                    permissionApply(
                        Manifest.permission.READ_CONTACTS,
                        Constraint.PERMISSION_READ_CONTACTS_CODE,
                        getString(R.string.permission_read_contacts_apply_message),
                        getString(R.string.permission_read_contacts_apply_denied)
                    )
                } else {
                    intentSelectContact()
                }

            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            Constraint.INTENT_READ_CONTACTS_CODE -> {
                val contact = getContact(binding.root.context, data)
                Timber.i("contact: \n $contact")
                this.preferenceSet("phone", contact.phone)
            }
        }
    }

    fun getContact(context: Context, data: Intent?): Contact {
        val contact = Contact()

        if (data!!.data == null) return contact

        val contentResolver = context.contentResolver
        val uri = data.data

        val contacts = contentResolver.query(uri!!, null, null, null, null)
        if (contacts!!.moveToFirst()) {
            val id: String = contacts.getString(contacts.getColumnIndex(Contacts._ID))
                ?: return contact

            contact.name = contacts
                .getStringOrNull(contacts.getColumnIndex(Contacts.DISPLAY_NAME))
                .let { str ->
                    if (!str.isNullOrEmpty() && !str.contains("\\d+".toRegex()))
                        str
                    else
                        context.getString(R.string.safety_contact)
                }

            val phones = contentResolver.query(
                Phone.CONTENT_URI, null, "${Phone.CONTACT_ID} = $id", null, null
            )

            while (phones!!.moveToNext()) {
                val type = phones.getInt(phones.getColumnIndex(Phone.TYPE))
                contact.phone = phones.getStringOrNull(phones.getColumnIndex(Phone.NUMBER))
                    .takeIf {
                        type == Phone.TYPE_MOBILE
                                || type == Phone.TYPE_WORK_MOBILE
                                || type == Phone.TYPE_MAIN
                    }
                    .let { str -> str ?: "" }
            }

            phones.close()
        }

        contacts.close()

        return contact
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
