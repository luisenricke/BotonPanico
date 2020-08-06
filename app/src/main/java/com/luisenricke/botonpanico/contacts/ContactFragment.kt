package com.luisenricke.botonpanico.contacts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentContactBinding
import timber.log.Timber

class ContactFragment : BaseFragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

        contactAdapter = setContactAdapter(binding.root.context)

        binding.apply {
            btnAddContact.setOnClickListener {
                val action = ContactFragmentDirections.actionContactToContactAdd()
                navController.navigate(action)
            }

            recyclerContacts.apply {
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy > 0 && btnAddContact.visibility == View.VISIBLE) {
                            btnAddContact.hide()
                            getActivityContext().setBottomNavigationViewVisibility(false)
                        } else if (dy < 0 && btnAddContact.visibility != View.VISIBLE) {
                            btnAddContact.show()
                            getActivityContext().setBottomNavigationViewVisibility(true)
                        }
                    }
                })

                adapter = contactAdapter
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        contactAdapter.updateList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setContactAdapter(context: Context): ContactAdapter {
        val clickListener: (Contact) -> Unit = { item ->
            val action = ContactFragmentDirections.actionContactToContactDetail()
            action.idContact = item.id
            navController.navigate(action)
        }

        val longClickListener: (Contact) -> Unit = { item ->
            Timber.i("longClicked")
        }

        return ContactAdapter(context, clickListener, longClickListener)
    }
}
