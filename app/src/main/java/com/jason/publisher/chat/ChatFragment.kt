package com.jason.publisher.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jason.publisher.AdapterClasses.ChatAdapter
import com.jason.publisher.databinding.FragmentChatBinding

class ChatFragment(private var chatList : ArrayList<Chat>) : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListChat()
        setRecyclerList()
    }

    private fun setRecyclerList() {
        binding.rvChat.setHasFixedSize(true)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        val listChatAdapter = ChatAdapter(chatList)
        binding.rvChat.adapter = listChatAdapter
    }

    private fun getListChat() {
        chatList.add(Chat(message = "Hi", timestamp = "21.30"))
        chatList.add(Chat(message = "Hallo", timestamp = "21.30"))
        chatList.add(Chat(message = "Test", timestamp = "21.30"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}