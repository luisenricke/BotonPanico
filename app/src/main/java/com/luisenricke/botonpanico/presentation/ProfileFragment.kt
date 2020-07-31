package com.luisenricke.botonpanico.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.database.getStringOrNull
import com.luisenricke.androidext.*
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentProfileBinding
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

            val imageStorage = root.context.loadImageInternalStorage(Constraint.PROFILE_PHOTO)
            if (imageStorage != null) imgProfile.setImageBitmap(imageStorage)

            contact.setOnClickListener { checkContactsPermission() }
            btnGallery.setOnClickListener {
                intentSelectImageFromGallery(Constraint.INTENT_IMAGE_FROM_GALLERY)
            }
            btnDeleteImage.setOnClickListener {
                imgProfile.setImageResource(R.drawable.ic_baseline_person_24)
                val isDeleted = context?.deleteImageInternalStorage(Constraint.PROFILE_PHOTO)
                Timber.i("Is profile photo deleted? $isDeleted")
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
            Constraint.INTENT_IMAGE_FROM_GALLERY -> {
                val image = getImage(binding.root.context, data) ?: return
                binding.imgProfile.setImageBitmap(image)
                val isSaved = binding.root.context
                    .saveImageInternalStorage(image, Constraint.PROFILE_PHOTO)
                Timber.i("Photo saved: $isSaved")
            }
            Constraint.INTENT_READ_CONTACTS_CODE -> {
                val contact = getContact(binding.root.context, data)
                Timber.i("contact: \n $contact")
                this.preferenceSet("phone", contact.phone)
            }
        }
    }

    // TODO test this
    fun getImage(context: Context, data: Intent?): Bitmap? {
        val contentURI: Uri = data?.data!!
        var bitmap: Bitmap? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, contentURI)
            bitmap = ImageDecoder.decodeBitmap(source)

        } else {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentURI)
        }

        return bitmap!!
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

                    if (!str.isNullOrEmpty() && !str.contains("\\d+".toRegex())) {
                        str
                    } else {
                        context.getString(R.string.safety_contact)
                    }

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
