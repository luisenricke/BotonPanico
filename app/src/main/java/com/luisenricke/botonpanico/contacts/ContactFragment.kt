package com.luisenricke.botonpanico.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.androidext.toastShort
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentContactBinding

class ContactFragment : BaseFragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private val list = arrayListOf(
        Contact(
            phone = "123456789",
            name = "Luis Enrique Villalobos Meléndez",
            relationship = "test1",
            id = 1
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 2
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 3
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 4
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 5
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 6
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 7
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 8
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 9
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 10
        ),
        Contact(
            phone = "987456321",
            name = "Paola Nashely Osorio Guzman",
            relationship = "test2",
            id = 11
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

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
                adapter = ContactAdapter(list)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
