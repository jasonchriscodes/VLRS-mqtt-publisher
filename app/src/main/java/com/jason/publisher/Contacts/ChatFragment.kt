package com.jason.publisher.Contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jason.publisher.AdapterClasses.ChatAdapter
import com.jason.publisher.databinding.FragmentChatBinding
import java.util.Objects

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private var chatList = ArrayList<Chat>()
    private val db = Firebase.firestore

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
        db.collection("chats").get()
            .addOnSuccessListener {result ->
                for (data in result.documents) {
                    chatList.add(Chat(message = data.data!!["name"].toString(), timestamp = "21.30"))
                }
                setRecyclerList()
            }
            .addOnFailureListener {
                chatList.add(Chat(message =  "Error", timestamp = "21.30", isSender = false))
                setRecyclerList()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}