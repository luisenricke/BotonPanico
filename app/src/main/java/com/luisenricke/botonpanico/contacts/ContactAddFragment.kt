package com.luisenricke.botonpanico.contacts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.luisenricke.androidext.saveImageInternalStorage
import com.luisenricke.androidext.toastShort
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.Constraint
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentContactAddBinding
import com.luisenricke.botonpanico.service.SendSMS
import com.luisenricke.kotlinext.formatPhone
import com.luisenricke.kotlinext.removeWhiteSpaces
import timber.log.Timber
import com.luisenricke.botonpanico.contacts.ContactUtils as utils

class ContactAddFragment : BaseFragment() {

    private var _binding: FragmentContactAddBinding? = null
    private val binding get() = _binding!!

    private var image: Bitmap? = null
    private var isSetImage: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getActivityContext().setBottomNavigationViewVisibility(false)
        _binding = FragmentContactAddBinding.inflate(inflater, container, false)

        binding.apply {
            val context = root.context

            // Toolbar
            getActivityContext().setSupportActionBar(toolbar)
            setupActionBar(getActivityContext().supportActionBar, getString(R.string.contact_add))

            // Image
            imgProfile.setOnClickListener {
                imageOptionsSimple(root.context, imgProfile, image)
                isSetImage = utils.checkDefaultImage(context, imgProfile.drawable)
                if (!isSetImage) image = null
            }

            //Name
            txtName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) utils.hasEmptyField(context, txtLayoutName, txtName.text!!)
            }

            // Phone
            txtPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
            txtPhone.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    utils.hasEmptyField(context, txtLayoutPhone, txtPhone.text!!)
                    utils.hasValidPhoneField(context, txtLayoutPhone, txtPhone.text!!)
                }
            }

            // Relationship
            val adapter = ArrayAdapter(context, R.layout.item_relationship, context.resources.getStringArray(R.array.contact_relationship_list))

            (txtLayoutRelationship.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            txtRelationship.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) getActivityContext().keyboard.hide(v)
                else utils.hasEmptyField(context, txtLayoutRelationship, txtRelationship.text!!)
            }

            // Message
            txtLayoutMessage.counterMaxLength = SendSMS.getInstance(context).getMaxLength()

            // Switch
            val highlightedList = database.contactDAO().countHighlighted()
            if (highlightedList >= 5) {
                swtHighlighted.visibility = View.GONE
            }

            // Buttons
            btnCancel.setOnClickListener {
                imgProfile.setImageResource(R.drawable.ic_baseline_person_24)
                navController.popBackStack()
            }

            btnAccept.setOnClickListener {
                val isEmptyName = utils.hasEmptyField(context, txtLayoutName, txtName.text!!)
                val isEmptyPhone = utils.hasEmptyField(context, txtLayoutPhone, txtPhone.text!!)
                val isEmptyRelationShip = utils.hasEmptyField(context, txtLayoutRelationship, txtRelationship.text!!)
                val isPhoneValid = utils.hasValidPhoneField(context, txtLayoutPhone, txtPhone.text!!)

                if (!isEmptyName && !isEmptyPhone && !isEmptyRelationShip && !isPhoneValid) {
                    val formatPhone = txtPhone.text.toString().removeWhiteSpaces().formatPhone("")
                    val name = txtName.text.toString()

                    var imageSrc = ""
                    if (isSetImage) {
                        imageSrc = "${formatPhone}_${name.removeWhiteSpaces()}"
                        val isSavedImage = binding.root.context.saveImageInternalStorage(image, imageSrc)
                        if (!isSavedImage) imageSrc = ""
                    }

                    val contact = Contact(
                            name = name,
                            phone = formatPhone,
                            isHighlighted = swtHighlighted.isChecked,
                            relationship = txtRelationship.text.toString(),
                            message = txtMessage.text.toString(),
                            image = imageSrc
                    )
                    Timber.i(contact.toString())
                    database.contactDAO().insert(contact)
                    toastShort(context.getString(R.string.contact_added_successfully))
                    navController.popBackStack()
                }
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            Constraint.INTENT_READ_CONTACTS_CODE -> {
                val contact = utils.getContact(binding.root.context, data)
                Timber.d("Imported contact: \n $contact")

                binding.txtName.setText(contact.name)
                utils.hasEmptyField(binding.root.context, binding.txtLayoutName, binding.txtName.text!!)

                binding.txtPhone.setText(contact.phone)
                utils.hasEmptyField(binding.root.context, binding.txtLayoutPhone, binding.txtPhone.text!!)
                utils.hasValidPhoneField(binding.root.context, binding.txtLayoutPhone, binding.txtPhone.text!!)
            }
            Constraint.INTENT_IMAGE_FROM_GALLERY -> {
                image = getImage(binding.root.context, data) ?: return
                Timber.d("The image was set with ${image?.width}x${image?.height}")

                isSetImage = true
                binding.imgProfile.setImageBitmap(image)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_contact_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home        -> {
                navController.popBackStack()
                true
            }
            R.id.menu_contact_import -> {
                utils.requestContact(this)
                true
            }
            else                     -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getActivityContext().setBottomNavigationViewVisibility(true)
        _binding = null
    }
}
