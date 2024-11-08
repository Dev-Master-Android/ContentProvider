package com.example.contentprovider

import android.annotation.SuppressLint
import com.example.contentprovider.databinding.ItemContactBinding
import com.example.contentprovider.model.Contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
class ContactsAdapter(
    private var contacts: List<Contact>,
    private val onContactAction: (Contact, Action) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    enum class Action {
        CALL, MESSAGE
    }

    class ContactViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.binding.contactName.text = contact.name
        holder.binding.callButton.setOnClickListener {
            onContactAction(contact, Action.CALL)
        }
        holder.binding.messageButton.setOnClickListener {
            onContactAction(contact, Action.MESSAGE)
        }
    }

    override fun getItemCount() = contacts.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(filteredContacts: List<Contact>) {
        contacts = filteredContacts
        notifyDataSetChanged()
    }
}
