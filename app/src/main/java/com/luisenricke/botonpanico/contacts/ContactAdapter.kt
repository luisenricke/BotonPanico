package com.luisenricke.botonpanico.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luisenricke.androidext.loadImageInternalStorage
import com.luisenricke.botonpanico.R
import com.luisenricke.botonpanico.database.AppDatabase
import com.luisenricke.botonpanico.database.dao.ContactDAO
import com.luisenricke.botonpanico.database.entity.Contact
import com.luisenricke.botonpanico.databinding.ItemContactBinding

class ContactAdapter(
        val context: Context,
        val clickListener: (Contact) -> Unit,
        val longClickListener: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var contacts: List<Contact> = arrayListOf()
    private val dao: ContactDAO = AppDatabase.getInstance(context).contactDAO()

    init {
        updateList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) = holder.bind(contacts[pos])

    inner class ViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            with(binding) {
                lblId.text = contact.id.toString()
                lblName.text = contact.name
                lblRelationship.text = contact.relationship
                cardContact.isChecked = contact.isHighlighted

                if (contact.image.isNotEmpty()) {
                    val image = root.context.loadImageInternalStorage(contact.image)
                    imgProfile.setImageBitmap(image)
                } else {
                    imgProfile.setImageResource(R.drawable.ic_baseline_person_24)
                }

                cardContact.setOnClickListener {
                    clickListener(contact)
                }

                cardContact.setOnLongClickListener {
                    longClickListener(contact)

                    val check = !contact.isHighlighted
                    contact.isHighlighted = check
                    cardContact.isChecked = check

                    dao.update(contact)

                    updateList()
                    true
                }
            }
        }
    }

    fun updateList() {
        contacts = dao.get().sortedBy { it.name }.sortedByDescending { it.isHighlighted }
        notifyDataSetChanged()
    }
}
