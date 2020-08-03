package com.luisenricke.botonpanico.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.ItemContactBinding
import timber.log.Timber

class ContactAdapter(
    private val contacts: List<Contact>
) :
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
                lblRelationship.text = contact.relationship

//                btnDelete.setOnClickListener { }
//                root.setOnClickListener { clickListener(contact) }
                cardContact.setOnClickListener {
                    Timber.i("clicked contact")
                }

                cardContact.setOnLongClickListener {
                    Timber.i("longcliked contact")
                    val checked = !cardContact.isChecked
                    cardContact.isChecked = checked
                    true
                }
            }
        }
    }
}
