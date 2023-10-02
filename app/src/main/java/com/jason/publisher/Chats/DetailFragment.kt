package com.jason.publisher.Chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jason.publisher.AdapterClasses.ChatAdapter
import com.jason.publisher.AdapterClasses.ContentChatAdapter
import com.jason.publisher.Contacts.Chat
import com.jason.publisher.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var chatList = ArrayList<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getListChat()
        setRecyclerList()
    }

    private fun setRecyclerList() {
        binding.rvChatBubble.setHasFixedSize(true)
        binding.rvChatBubble.layoutManager = LinearLayoutManager(requireContext())
        val listChatAdapter = ContentChatAdapter(chatList)
        binding.rvChatBubble.adapter = listChatAdapter
    }

    private fun getListChat() {
        chatList.add(Chat(message = "Hi", timestamp = "21.30", isSender = true))
        chatList.add(Chat(message = "Hallo", timestamp = "21.30", isSender = false))
        chatList.add(Chat(message = "Test", timestamp = "21.30", isSender = true))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}