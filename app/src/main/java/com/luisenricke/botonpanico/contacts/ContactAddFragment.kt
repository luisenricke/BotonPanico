package com.luisenricke.botonpanico.contacts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.database.getStringOrNull
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentContactAddBinding
import com.luisenricke.botonpanico.service.SendSMS
import timber.log.Timber

class ContactAddFragment : BaseFragment() {

    private var _binding: FragmentContactAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getActivityContext().setBottomNavigationViewVisibility(false)
        _binding = FragmentContactAddBinding.inflate(inflater, container, false)

        binding.apply {
            getActivityContext().setSupportActionBar(toolbar)
            setupActionBar(getActivityContext().supportActionBar, getString(R.string.contact_add))

            // Phone
            txtPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

            // Relationship
            val adapter = ArrayAdapter(
                root.context,
                R.layout.item_relationship,
                root.context.resources.getStringArray(R.array.contact_relationship_list)
            )

            (txtLayoutRelationship.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            txtRelationship.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) getActivityContext().keyboard.hide(v)
            }

            // Message
            txtLayoutMessage.apply {
                counterMaxLength = SendSMS.getInstance(root.context).getMaxLength()
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
                binding.txtName.setText(contact.name)
                binding.txtPhone.setText(contact.phone)
                Timber.i("contact: \n $contact")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_contact_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.popBackStack(R.id.nav_contact, true)
                navController.navigate(R.id.nav_contact)
                true
            }
            R.id.menu_contact_import -> {
                checkContactsPermission()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        getActivityContext().setBottomNavigationViewVisibility(true)
    }

    // TODO get image of the contact if has
    fun getContact(context: Context, data: Intent?): Contact {
        val contact = Contact()

        if (data!!.data == null) return contact

        val contentResolver = context.contentResolver
        val uri = data.data

        val contacts = contentResolver.query(uri!!, null, null, null, null)
        if (contacts!!.moveToFirst()) {
            val id: String =
                contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID))
                    ?: return contact

            contact.name = contacts
                .getStringOrNull(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                .let { str ->

                    if (!str.isNullOrEmpty() && !str.contains("\\d+".toRegex())) {
                        str
                    } else {
                        context.getString(R.string.safety_contact)
                    }

                }

            val phones = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $id",
                null,
                null
            )

            while (phones!!.moveToNext()) {
                val type =
                    phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                contact.phone =
                    phones.getStringOrNull(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .takeIf {
                            type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                                    || type == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE
                                    || type == ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
                        }
                        .let { str -> str ?: "" }
            }

            phones.close()
        }

        contacts.close()

        return contact
    }
}
