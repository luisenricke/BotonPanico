package com.luisenricke.botonpanico.contacts

import android.os.Bundle
import android.view.*
import com.luisenricke.androidext.toastLong
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.databinding.FragmentContactDetailBinding

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

            if (idContact != 0L) {
                toastLong("YESSS $idContact")
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
