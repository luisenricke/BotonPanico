package com.luisenricke.botonpanico.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.dao.ContactDAO
import com.luisenricke.botonpanico.databinding.FragmentContactBinding
import timber.log.Timber

class ContactFragment : BaseFragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private val dao: ContactDAO
        get() = database.contactDAO()

    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

        contactAdapter = ContactAdapter(binding.root.context, { item ->
//            Timber.i("Clicked from Fragment ${item.name}")
        }, { item ->
//            Timber.i("LongClicked from Fragment ${item.name}")
        })

        binding.apply {
            btnAddContact.setOnClickListener {
                navController.navigate(R.id.action_contact_to_contact_add)
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
}
