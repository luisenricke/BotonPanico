package com.luisenricke.botonpanico.contacts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.luisenricke.androidext.*
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentContactEditBinding
import com.luisenricke.botonpanico.service.SendSMS
import com.luisenricke.kotlinext.formatPhone
import com.luisenricke.kotlinext.removeWhiteSpaces
import timber.log.Timber

class ContactEditFragment : BaseFragment() {

    private var _binding: FragmentContactEditBinding? = null
    private val binding get() = _binding!!

    private var idContact: Long = 0L

    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val args = ContactEditFragmentArgs.fromBundle(it)
            idContact = args.idContact
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentContactEditBinding.inflate(inflater, container, false)

        binding.apply {
            val context = root.context
            val dao = database.contactDAO()

            //  region Toolbar
            getActivityContext().setSupportActionBar(toolbar)
            setupActionBar(getActivityContext().supportActionBar, getString(R.string.contact_edit))
            // endregion Toolbar

            // region GetContact
            val contact = dao.get(idContact)

            if (idContact == 0L || contact == null) {
                toastLong(context.getString(R.string.contact_empty))
                navController.popBackStack()
            }
            // endregion

            // region InitData
            contact?.let {
                if (contact.image.isNotEmpty()) {
                    val image = context.loadImageInternalStorage(contact.image)
                    imgProfile.setImageBitmap(image)
                    imgProfile.tag = context.getString(R.string.photo_image_tag_object)
                } else {
                    imgProfile.setImageResource(R.drawable.ic_baseline_person_24)
                    imgProfile.tag = context.getString(R.string.photo_image_tag_default)
                }

                txtName.setText(contact.name)
                txtPhone.setText(contact.phone.formatPhone(" "))
                txtRelationship.setText(contact.relationship)
                txtMessage.setText(contact.message)
                swtHighlighted.isChecked = contact.isHighlighted
            }
            // endregion PutData

            // region BindViews
            // Image
            imgProfile.setOnClickListener {
                imageOptionsSimple(context, imgProfile)
            }

            //Name
            txtName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) ContactUtils.hasEmptyField(context, txtLayoutName, txtName.text!!)
            }

            // Phone
            txtPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
            txtPhone.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    ContactUtils.hasEmptyField(context, txtLayoutPhone, txtPhone.text!!)
                    ContactUtils.hasValidPhoneField(context, txtLayoutPhone, txtPhone.text!!)
                }
            }

            // Relationship
            val adapter = ArrayAdapter(context, R.layout.item_relationship, context.resources.getStringArray(R.array.contact_relationship_list))

            (txtLayoutRelationship.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            txtRelationship.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) getActivityContext().keyboard.hide(v)
                else ContactUtils.hasEmptyField(context, txtLayoutRelationship, txtRelationship.text!!)
            }

            // Message
            txtLayoutMessage.counterMaxLength = SendSMS.getInstance(context).getMaxLength()

            // Switch
            val highlightedList = dao.countHighlighted()
            if (highlightedList >= 5) {
                swtHighlighted.visibility = View.GONE
            }

            // Buttons
            btnCancel.setOnClickListener {
                navController.popBackStack()
            }

            btnAccept.setOnClickListener {
                val isNameEmpty = ContactUtils.hasEmptyField(context, txtLayoutName, txtName.text!!)
                val isPhoneEmpty = ContactUtils.hasEmptyField(context, txtLayoutPhone, txtPhone.text!!)
                val isRelationshipEmpty = ContactUtils.hasEmptyField(context, txtLayoutRelationship, txtRelationship.text!!)
                val isPhoneValid = ContactUtils.hasValidPhoneField(context, txtLayoutPhone, txtPhone.text!!)
                val isPhoneAlreadyExist = ContactUtils.hasPhoneAlreadyExist(context, dao, contact?.phone!!, txtLayoutPhone, txtPhone.text!!)

                if (!isNameEmpty && !isPhoneEmpty && !isRelationshipEmpty && !isPhoneValid && !isPhoneAlreadyExist) {
                    val formatPhone = txtPhone.text.toString().removeWhiteSpaces().formatPhone("")
                    val name = txtName.text.toString()

                    val editContact = Contact(
                            id = idContact,
                            name = name,
                            phone = formatPhone,
                            isHighlighted = swtHighlighted.isChecked,
                            relationship = txtRelationship.text.toString(),
                            message = txtMessage.text.toString(),
                            image = contact.image
                    )

                    dao.update(editContact)

                    if (imgProfile.tag == context.getString(R.string.photo_image_tag_custom)) {
                        val isDeletedImage = context.deleteImageInternalStorage(contact.image)
                        if (isDeletedImage || contact.image.isEmpty()) {
                            val imageSrc = "${idContact}_${formatPhone}_image"
                            val isSavedImage = context.saveImageInternalStorage(image, imageSrc)
                            editContact.image = if (isSavedImage) imageSrc else ""
                            dao.update(editContact)
                        } else {
                            Timber.e(context.getString(R.string.contact_edit_image_cannot_be_updated))
                        }
                    }

                    if (imgProfile.tag == context.getString(R.string.photo_image_tag_default)) {
                        val isDeletedImage = context.deleteImageInternalStorage(contact.image)
                        if (isDeletedImage || contact.image.isEmpty()) {
                            editContact.image = ""
                            dao.update(editContact)
                        } else {
                            Timber.e(context.getString(R.string.contact_edit_image_cannot_be_updated))
                        }
                    }

                    Timber.v("Contact: ${dao.get(idContact)}")
                    toastShort(context.getString(R.string.contact_edit_successfully))
                    navController.popBackStack()
                }
            }
            // endregion BindViews

        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            Constraint.INTENT_IMAGE_FROM_GALLERY -> {
                image = getImage(binding.root.context, data) ?: return
                Timber.v("The image was set with ${image?.width}x${image?.height}")

                binding.imgProfile.setImageBitmap(image)
                binding.imgProfile.tag = binding.root.context.getString(R.string.photo_image_tag_custom)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController.popBackStack()
                true
            }
            else              -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
