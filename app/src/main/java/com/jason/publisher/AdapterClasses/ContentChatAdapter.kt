package com.jason.publisher.AdapterClasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.publisher.databinding.LayoutChatLeftBinding
import com.jason.publisher.databinding.LayoutChatRightBinding
import com.jason.publisher.model.Chat

class ContentChatAdapter(private val dataList: List<Chat>, private val deviceName: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ListViewHolderRight(val binding: LayoutChatRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItemSender(data: Chat) {
            binding.text.text = data.message
        }
    }

    inner class ListViewHolderLeft(val binding: LayoutChatLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItemReceiver(data: Chat) {
            binding.text.text = data.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val binding = LayoutChatRightBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ListViewHolderRight(binding)
        } else {
            val binding = LayoutChatLeftBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ListViewHolderLeft(binding)
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        val data = dataList[position]
        return if (data.sender == deviceName) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]
        if (data.sender == deviceName) {
            (holder as ListViewHolderRight).bindItemSender(data)
        } else {
            (holder as ListViewHolderLeft).bindItemReceiver(data)
        }
    }

    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2
    }
}
