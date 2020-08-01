package com.luisenricke.botonpanico.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.ItemContactBinding

class ContactAdapter(private val contacts: List<Contact>, val clickListener: (Contact) -> Unit) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    inner class ViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            with(binding) {
                lblId.text = contact.id.toString()
                lblName.text = contact.name
                lblPhone.text = contact.phone

                btnDelete.setOnClickListener { }
                root.setOnClickListener { clickListener(contact) }
            }
        }
    }
}
