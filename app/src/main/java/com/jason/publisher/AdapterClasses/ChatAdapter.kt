package com.jason.publisher.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.publisher.model.Contact
import com.jason.publisher.databinding.ItemListChatBinding
import com.jason.publisher.`interface`.ContactClickListener

class ChatAdapter(private val dataList: ArrayList<Contact>, private val context: Context): RecyclerView.Adapter<ChatAdapter.ListViewHolder>() {

    inner class ListViewHolder(val binding: ItemListChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: Contact) {
            binding.chatPreview.text = data.message
            binding.chatDate.text = data.timestamp

            binding.root.setOnClickListener {
                val listener = context as ContactClickListener
                listener.onContackClicked(data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemListChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = dataList[position]
        holder.bindItem(data)
    }
}