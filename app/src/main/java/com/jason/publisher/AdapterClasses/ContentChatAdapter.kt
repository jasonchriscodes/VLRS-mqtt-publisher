package com.jason.publisher.AdapterClasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.publisher.Contacts.Chat
import com.jason.publisher.databinding.ItemListBubbleBinding

class ContentChatAdapter(private val dataList: ArrayList<Chat>): RecyclerView.Adapter<ContentChatAdapter.ListViewHolder>() {

    inner class ListViewHolder(val binding: ItemListBubbleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItemReceiver(data: Chat) {
            binding.textMessageBody.text = data.message
            if (data.isSender) {

            } else {

            }
        }
        fun bindItemSender(data: Chat) {
            binding.textMessageBody.text = data.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemListBubbleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = dataList[position]
        if (data.isSender) {
            holder.bindItemReceiver(data)
        } else {
            holder.bindItemSender(data)
        }
    }
}