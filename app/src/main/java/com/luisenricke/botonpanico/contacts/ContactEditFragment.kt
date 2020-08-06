package com.luisenricke.botonpanico.contacts

import android.os.Bundle
import android.view.*
import com.luisenricke.androidext.toastLong
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.databinding.FragmentContactEditBinding

class ContactEditFragment : BaseFragment() {

    private var _binding: FragmentContactEditBinding? = null
    private val binding get() = _binding!!

    private var idContact: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentContactEditBinding.inflate(inflater, container, false)

        arguments?.let {
            val args = ContactEditFragmentArgs.fromBundle(it)
            idContact = args.idContact
        }

        binding.apply {
            val context = root.context

            // Toolbar
            getActivityContext().setSupportActionBar(toolbar)
            setupActionBar(getActivityContext().supportActionBar, getString(R.string.contact_edit))

            if (idContact != 0L) {
                toastLong("YESSS $idContact")
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
