package com.luisenricke.botonpanico.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.luisenricke.androidext.Toast
import com.luisenricke.botonpanico.adapter.ContactAdapter
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.FragmentContactBinding


class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private val list = arrayListOf(
        Contact("123456789", "test", 1),
        Contact("987456321", "luis", 2)
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
                    Toast.short(root.context, "clicked ${item.name}")
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
