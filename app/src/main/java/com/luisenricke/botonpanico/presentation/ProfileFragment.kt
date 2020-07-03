package com.luisenricke.botonpanico.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luisenricke.androidext.Toast
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.databinding.FragmentProfileBinding
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import androidx.core.database.getStringOrNull
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact

class ProfileFragment : Fragment() {

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
                val intent = Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI)
                startActivityForResult(intent, Constraint.READ_CONTACTS_REQUEST)
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            // Toast.short(binding.root.context, "Cancelled")
            return
        }

        when (requestCode) {
            Constraint.READ_CONTACTS_REQUEST -> {
                getContact(binding.root.context, data)
            }
        }
    }

    private fun getContact(context: Context, data: Intent?): Contact {
        if (data!!.data == null) throw NullPointerException("Empty data to get Contact")

        val contact = Contact()
        val contentResolver = context.contentResolver
        val uri = data.data

        val contacts = contentResolver.query(uri!!, null, null, null, null)
        if (contacts!!.moveToFirst()) {
            val id: String = contacts.getString(contacts.getColumnIndex(Contacts._ID))
                ?: throw NullPointerException("Doesn't find id")
            contact.name = contacts.getStringOrNull(contacts.getColumnIndex(Contacts.DISPLAY_NAME))
                ?: context.getString(R.string.safety_contact)

            val phones = contentResolver.query(
                Phone.CONTENT_URI, null, "${Phone.CONTACT_ID} = $id", null, null
            )

            while (phones!!.moveToNext()) {
                val type = phones.getInt(phones.getColumnIndex(Phone.TYPE))
                contact.phone = phones.getString(phones.getColumnIndex(Phone.NUMBER))
                    .takeIf { type == Phone.TYPE_MOBILE || type == Phone.TYPE_WORK_MOBILE }
                    ?: throw NullPointerException("Doesn't find phone")
            }

            phones.close()
        }
        contacts.close()

        Toast.short(context, "Contacto: ${contact.name} - ${contact.phone}")

        return contact
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
