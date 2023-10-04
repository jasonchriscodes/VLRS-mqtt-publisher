package com.jason.publisher.AdapterClasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.publisher.Contacts.Chat
import com.jason.publisher.databinding.ItemListChatBinding

class ChatAdapter(private val dataList: ArrayList<Chat>): RecyclerView.Adapter<ChatAdapter.ListViewHolder>() {

    inner class ListViewHolder(val binding: ItemListChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: Chat) {
            binding.chatPreview.text = data.message
            binding.chatDate.text = data.timestamp

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