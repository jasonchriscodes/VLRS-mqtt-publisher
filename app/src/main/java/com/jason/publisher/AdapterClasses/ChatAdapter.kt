package com.jason.publisher.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.publisher.model.Contact
import com.jason.publisher.databinding.ItemListChatBinding
import com.jason.publisher.`interface`.ContactClickListener

/**
 * Adapter class for displaying chat items in a RecyclerView.
 *
 * @param dataList List of contact data to be displayed.
 * @param context Context of the parent activity or fragment.
 */
class ChatAdapter(private val dataList: ArrayList<Contact>, private val context: Context): RecyclerView.Adapter<ChatAdapter.ListViewHolder>() {

    /**
     * ViewHolder class for chat items.
     *
     * @param binding Binding object for the chat item layout.
     */
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

    /**
     * Creates a new ViewHolder for a chat item.
     *
     * @param parent Parent view group.
     * @param viewType View type of the new View.
     * @return A new ListViewHolder that holds a view of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemListChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Returns the total number of items in the data list.
     *
     * @return The size of the data list.
     */
    override fun getItemCount(): Int = dataList.size

    /**
     * Binds the data to the ViewHolder at the specified position.
     *
     * @param holder ViewHolder to bind data to.
     * @param position Position of the data in the data list.
     */
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = dataList[position]
        holder.bindItem(data)
    }
}