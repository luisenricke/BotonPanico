package com.luisenricke.botonpanico.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.luisenricke.androidext.toastShort
import com.luisenricke.botonpanico.BaseFragment
import com.luisenricke.botonpanico.adapter.ContactAdapter
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentContactBinding

class ContactFragment : BaseFragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private val list = arrayListOf(
        Contact(phone = "123456789", name = "test", relationship = "test1", id = 1),
        Contact(phone = "987456321", name = "luis", relationship = "test2", id = 2)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

        binding.apply {
            recyclerContacts.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this.context)
                adapter = ContactAdapter(list) { item ->
                    toastShort("clicked ${item.name}")
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
