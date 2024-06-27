package com.jason.publisher.AdapterClasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.publisher.databinding.LayoutChatLeftBinding
import com.jason.publisher.databinding.LayoutChatRightBinding
import com.jason.publisher.model.Chat

/**
 * Adapter class for displaying chat messages in a RecyclerView.
 *
 * @param dataList List of chat data to be displayed.
 * @param deviceName Name of the current device to distinguish between sent and received messages.
 */
class ContentChatAdapter(private val dataList: List<Chat>, private val deviceName: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * ViewHolder class for chat messages sent by the user.
     *
     * @param binding Binding object for the right-aligned chat item layout.
     */
    inner class ListViewHolderRight(val binding: LayoutChatRightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the chat data to the right-aligned chat item views.
         *
         * @param data Chat data to be displayed.
         */
        fun bindItemSender(data: Chat) {
            binding.text.text = data.message
        }
    }

    /**
     * ViewHolder class for chat messages received by the user.
     *
     * @param binding Binding object for the left-aligned chat item layout.
     */
    inner class ListViewHolderLeft(val binding: LayoutChatLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItemReceiver(data: Chat) {
            binding.text.text = data.message
        }
    }

    /**
     * Creates a new ViewHolder for a chat item.
     *
     * @param parent Parent view group.
     * @param viewType View type of the new View.
     * @return A new RecyclerView.ViewHolder that holds a view of the given view type.
     */
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

    /**
     * Returns the total number of items in the data list.
     *
     * @return The size of the data list.
     */
    override fun getItemCount(): Int = dataList.size

    /**
     * Returns the view type for the item at the given position.
     *
     * @param position Position of the item in the data list.
     * @return The view type of the item at the given position.
     */
    override fun getItemViewType(position: Int): Int {
        val data = dataList[position]
        return if (data.sender == deviceName) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    /**
     * Binds the data to the ViewHolder at the specified position.
     *
     * @param holder ViewHolder to bind data to.
     * @param position Position of the data in the data list.
     */
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
