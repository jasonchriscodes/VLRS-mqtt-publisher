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
        chatList.add(Chat(message = "Hey Jason, how was your weekend?", timestamp = "21.30", isSender = true))
        chatList.add(Chat(message = "It was great! I went hiking and spent time with friends. How about you?", timestamp = "21.30", isSender = false))
        chatList.add(Chat(message = "Sounds fun! I had a relaxing weekend at home. Anything exciting coming up for you?", timestamp = "21.30", isSender = true))
        chatList.add(Chat(message =  "Not much, just work and some dinner plans this week. Anything fun on your agenda?", timestamp = "21.30", isSender = false))
        chatList.add(Chat(message = "I'm planning to check out a new movie and maybe try a new restaurant. Let me know if you'd like to join!", timestamp = "21.30", isSender = true))
        chatList.add(Chat(message = "That sounds awesome! I'll keep that in mind. Thanks for the invite!", timestamp = "21.30", isSender = false))
        chatList.add(Chat(message =  "No problem, anytime! Have a fantastic day!", timestamp = "21.30", isSender = true))
        chatList.add(Chat(message =  "You too! Take care and have a wonderful day!", timestamp = "21.30", isSender = false))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}