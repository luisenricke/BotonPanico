package com.luisenricke.botonpanico.contacts

import android.os.Bundle
import android.view.*
import com.luisenricke.androidext.loadImageInternalStorage
import com.luisenricke.androidext.toastLong
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.databinding.FragmentContactDetailBinding
import com.luisenricke.kotlinext.formatPhone

class ContactDetailFragment : BaseFragment() {

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    private var idContact: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = ContactDetailFragmentArgs.fromBundle(it)
            idContact = args.idContact
        }

        getActivityContext().setBottomNavigationViewVisibility(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)

        binding.apply {
            val context = root.context

            // Toolbar
            getActivityContext().setSupportActionBar(toolbar)
            setupActionBar(getActivityContext().supportActionBar, getString(R.string.contact_detail))

            // contact
            val contact = database.contactDAO().get(idContact)

            if (idContact == 0L || contact == null) {
                toastLong("Something happen with the contact")
                navController.popBackStack()
            }

            contact?.let {
                if (contact.image.isNotEmpty()) {
                    val image = context.loadImageInternalStorage(contact.image)
                    imgProfile.setImageBitmap(image)
                } else {
                    imgProfile.setImageResource(R.drawable.ic_baseline_person_24)
                }

                lblName.text = contact.name
                lblPhone.text = contact.phone.formatPhone(" ")
                lblRelationship.text = contact.relationship

                if (contact.message.isNotEmpty()) {
                    lblMessage.text = contact.message
                } else {
                    lblMessage.text = context.getString(R.string.contact_default_message)
                }

                if (contact.isHighlighted) {
                    lblHighlighted.text = context.getString(R.string.contact_highlighted_true)
                    imgHighlighted.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    lblHighlighted.text = context.getString(R.string.contact_highlighted_false)
                    imgHighlighted.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_contact_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home        -> {
                navController.popBackStack()
                true
            }
            R.id.menu_contact_edit   -> {
                val action = ContactDetailFragmentDirections.actionContactDetailToContactEdit()
                action.idContact = idContact
                navController.navigate(action)
                true
            }
            R.id.menu_contact_delete -> {
                true
            }
            else                     -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        getActivityContext().setBottomNavigationViewVisibility(true)
    }
}
